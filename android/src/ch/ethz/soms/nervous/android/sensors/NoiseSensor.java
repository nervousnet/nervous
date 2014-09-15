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

	public void dataReady() {
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
			int exp = binlog((int) (sampleDurationProd / 1000));
			buffersize = Math.max(AudioRecord.getMinBufferSize(SAMPPERSEC, CHANNEL, ENCODING), 2 ^ exp);
			buffer = new short[buffersize];
			buflen = buffersize / 2;
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPPERSEC, CHANNEL, ENCODING, buffersize);
			audioRecord.startRecording();
			samplesRead = audioRecord.read(buffer, 0, buffersize);
			audioRecord.stop();
			// FFT
			FFT fft = new FFT(buffersize);
			double[] window = fft.getWindow();
			double[] re = new double[buffersize];
			double[] im = new double[buffersize];
			fft.fft(re, im);
			// Pass data to listeners
			// Data: total noise level, 
			dataReady();
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
