package ch.ethz.soms.nervous.vm;

import java.io.IOException;
import java.io.InputStream;

public class LimitInputStream extends InputStream {

	private InputStream in;

	private long maxRead;

	private long read;

	public LimitInputStream(InputStream in, long maxRead) {
		this.in = in;
		this.maxRead = maxRead;
	}

	@Override
	public int read() throws IOException {
		if (read >= maxRead) {
			return -1;
		}
		int b = in.read();
		if (b != -1) {
			read++;
		}
		return b;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (read > maxRead) {
			return -1;
		}
		long left = getBytesLeft();
		if (len > left) {
			len = (int) left;
		}
		int bytesRead = in.read(b, off, len);
		read = read + bytesRead;
		return bytesRead;
	}

	@Override
	public long skip(long n) throws IOException {
		if (read > maxRead) {
			return -1;
		}
		long left = getBytesLeft();
		if (n > left) {
			n = left;
		}
		read = read + n;
		return in.skip(n);
	}
	
	@Override
	public void close() throws IOException {
		in.close();
	}

	private long getBytesLeft() {
		return maxRead - read;
	}

}
