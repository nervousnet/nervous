package ch.ethz.soms.nervous.android.sensors;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.soms.nervous.utils.FFT;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

public class NoiseSensor {

	public static final int SAMPPERSEC = 8000;
	public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	public static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;

	private short[] buffer;
	private int samplesRead;
	private int buflen;
	private int buffersize;
	private AudioRecord audioRecord;

	private List<NoiseListener> listenerList = new ArrayList<NoiseListener>();

	public interface NoiseListener {
		public void noiseSensorDataReady();
	}

	public void addListener(NoiseListener listener) {
		listenerList.add(listener);
	}

	public void dataReady(double spl) {
		for (NoiseListener listener : listenerList) {
			listener.noiseSensorDataReady();
		}
	}

	public class AudioTask extends AsyncTask<Long, Void, Void> {

		@Override
		protected Void doInBackground(Long... params) {
			// Recording
			long duration = params[0];
			long sampleDurationProd = duration * SAMPPERSEC;
			int exp1 = binlog((int) (sampleDurationProd / 1000));
			int exp2 = binlog(AudioRecord.getMinBufferSize(SAMPPERSEC, CHANNEL, ENCODING)) + 1;
			buffersize = (int) Math.pow(2, Math.max(exp1, exp2));
			buflen = buffersize / 2;
			buffer = new short[buffersize];
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPPERSEC, CHANNEL, ENCODING, buffersize);
			audioRecord.startRecording();
			samplesRead = audioRecord.read(buffer, 0, buffersize);
			audioRecord.stop();
			// FFT
			/*
			 * FFT fft = new FFT(buffersize); double[] window = fft.getWindow(); double[] re = new double[buffersize]; double[] im = new double[buffersize];
			 * 
			 * final double amp = 100.0; // factor 100 final int bpsample = 2; // 16bit PCM
			 * 
			 * for (int i = 0, fi = 0; i < buffersize; i++) { double sample = 0; for (int b = 0; b < bpsample; b++) { int v = buffer[i + b]; if (b < bpsample - 1 || bpsample == 1) { v = v & 0xFF; } sample = sample + (v << (b * 8)); } double sample32 = amp * (sample / 32768.0); re[i] = sample32; im[i] = 0; } fft.fft(re, im);
			 */

			double spl = 0.0;
			double rms = 0.0;

			for (int i = 0; i < buflen; i++) {
				rms = rms + ((int) (buffer[i]) * (int) (buffer[i]));
			}
			rms = Math.sqrt(rms / buflen);

			// See http://de.wikipedia.org/wiki/Schalldruckpegel
			double gain = 2500.0 / Math.pow(10.0, 90.0 / 20.0);
			spl = 20.d * Math.log10(gain * rms);

			// Pass data to listeners
			// Data: total noise level (spl in dB)
			dataReady(spl);
			return null;
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
