package ch.ethz.soms.nervous.vm;

import java.io.IOException;
import java.io.InputStream;

public class CountInputStream extends InputStream {

	private InputStream in;

	private long read;

	public CountInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public int read() throws IOException {
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
		int bytesRead = in.read(b, off, len);
		read = read + bytesRead;
		return bytesRead;
	}

	@Override
	public long skip(long n) throws IOException {
		read = read + n;
		return in.skip(n);
	}

	public long bytesRead() {
		return read;
	}

}
