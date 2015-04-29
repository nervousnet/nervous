package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.soms.nervous.utils.FFT;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

public class NoiseSensor {

	public static final int BANDCOUNT = 12;
	public static final int SAMPPERSEC = 8000; // 8000Hz sampling rate, the minimum
	public static final int NYQUIST = 4000; // Take this 4000Hz regardless of SAMPPERSEC
	public static final float BANDLOGBASE = (float) Math.exp(Math.log(NYQUIST) / BANDCOUNT); // Get the basis for the log structured frequency bands
	public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	public static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;

	private short[] buffer;
	private int samplesRead;
	private int buflen;
	private int fftlen;
	private int buffersize;
	private AudioRecord audioRecord;

	private List<NoiseListener> listenerList = new ArrayList<NoiseListener>();
	private Lock listenerMutex = new ReentrantLock();

	public interface NoiseListener {
		public void noiseSensorDataReady(long recordTime, float rms, float spl, float[] bands);
	}

	public void addListener(NoiseListener listener) {
		listenerMutex.lock();
		listenerList.add(listener);
		listenerMutex.unlock();
	}
	
	public void removeListener(NoiseListener listener) {
		listenerMutex.lock();
		listenerList.remove(listener);
		listenerMutex.unlock();
	}
	
	public void clearListeners() {
		listenerMutex.lock();
		listenerList.clear();
		listenerMutex.unlock();
	}

	public void dataReady(long recordTime, float rms, float spl, float[] bands) {
		listenerMutex.lock();
		for (NoiseListener listener : listenerList) {
			listener.noiseSensorDataReady(recordTime, rms, spl, bands);
		}
		listenerMutex.unlock();
	}

	public class AudioTask extends AsyncTask<Long, Void, Void> {
		private long recordTime;
		private float rms;
		private float spl;
		private float[] bands;

		@Override
		protected Void doInBackground(Long... params) {
			// Recording
			long duration = params[0];
			long sampleDurationProd = duration * SAMPPERSEC;
			int exp1 = binlog((int) (sampleDurationProd / 1000)) + 1;
			int exp2 = binlog(AudioRecord.getMinBufferSize(SAMPPERSEC, CHANNEL, ENCODING)) + 1;
			buflen = (int) Math.pow(2, Math.max(exp1, exp2));
			buffersize = buflen * 2;
			fftlen = buflen / 2;
			buffer = new short[buffersize];
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPPERSEC, CHANNEL, ENCODING, buffersize);
			long startTime = System.currentTimeMillis();
			audioRecord.startRecording();
			samplesRead = audioRecord.read(buffer, 0, buffersize);
			audioRecord.stop();
			long stopTime = System.currentTimeMillis();

			long recordTime = startTime + (stopTime - startTime) / 2;

			// Arrays for FFT
			double[] re = new double[buflen];
			double[] im = new double[buflen];

			// SPL, RMS
			double spl = 0.0;
			double rms = 0.0;

			for (int i = 0; i < buflen; i++) {
				re[i] = ((double) (buffer[i])) / 32768.d;
				rms = rms + Math.abs(buffer[i]);
			}
			rms = rms / buflen;

			// See for the pressure formula: http://de.wikipedia.org/wiki/Schalldruckpegel
			// See http://www.reddit.com/r/androiddev/comments/14bnrp/how_to_find_microphone_modelspec_of_android_device/
			// See "Android 4.0 compability definition guideline", chapter 5.3
			// Basically devices are required to conform to the equation:
			// --------- 20.d * Math.log10(gain * 2500) == 90.d
			// Thus deriving
			// --------- gain = Math.pow(10.0, 90.0 / 20.0) / 2500.0
			// and
			// --------- spl = 20.d * Math.log10(gain * rms);
			// (This is as close as we can get to absolute sound pressure levels (spl). The accuracy depends on how close the device is to Googles requirements.)
			//
			double gain = Math.pow(10.0, 90.0 / 20.0) / 2500.0;
			spl = 20.d * Math.log10(gain * rms);

			// FFT
			FFT fft = new FFT(buflen);
			double[] window = fft.getWindow();
			fft.fft(re, im);

			double[] power = new double[fftlen];
			for (int i = 0; i < fftlen; i++) {
				power[i] = (re[i] * re[i] - im[i] * im[i]) / (double) buflen;
			}

			double freqFact = (double) (SAMPPERSEC) / (double) buflen;
			// double freq = freqFact * peaki;

			// Logarithmic structured band counting, from 0Hz to 4000Hz
			// Frequency splits (given 12 bands): 2, 4, 8, 16, 32, 63, 126, 252, 503, 1004, 2004, 4000 (in Hz)
			float[] bands = new float[BANDCOUNT];
			for (int i = 0; i < BANDCOUNT; i++) {
				float avg = 0;
				int lowFreq = (int) Math.round((float) Math.pow(BANDLOGBASE, i));
				int hiFreq = (int) Math.round((float) Math.pow(BANDLOGBASE, i + 1));
				int fromIndex = (int) Math.round(((double) lowFreq) / freqFact);
				int toIndex = Math.min((int) Math.round(((double) hiFreq) / freqFact), fftlen);
				for (int j = fromIndex; j < toIndex; j++) {
					avg += Math.abs(power[j]);
				}
				avg = (toIndex > fromIndex) ? avg / (toIndex - fromIndex) : avg;
				bands[i] = avg;
			}

			// Pass data to listeners
			// Data: PCM RMS raw value, total noise level (spl in dB), log structured frequency bands
			this.recordTime = recordTime;
			this.rms = (float) rms;
			this.spl = (float) spl;
			this.bands = bands;
			return null;
		}

		@Override
		public void onPostExecute(Void params) {
			dataReady(recordTime, rms, spl, bands);
		}

	}

	public void startRecording(long duration) {
		new AudioTask().execute(duration);
	}

	private int binlog(int bits) {
		int log = 0;
		if ((bits & 0xffff0000) != 0) {
			bits >>>= 16;
			log = 16;
		}
		if (bits >= 256) {
			bits >>>= 8;
			log += 8;
		}
		if (bits >= 16) {
			bits >>>= 4;
			log += 4;
		}
		if (bits >= 4) {
			bits >>>= 2;
			log += 2;
		}
		return log + (bits >>> 1);
	}
}
