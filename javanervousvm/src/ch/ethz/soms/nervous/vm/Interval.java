package ch.ethz.soms.nervous.vm;

import java.io.Serializable;

public class Interval implements Comparable<Interval>, Serializable {
	private static final long serialVersionUID = 1255883524821812371L;

	public Interval(long lower, long upper) {
		this.lower = lower;
		this.upper = upper;
	}

	private long lower;
	private long upper;

	public Long getLower() {
		return lower;
	}

	public void setLower(long lower) {
		this.lower = lower;
	}

	public Long getUpper() {
		return upper;
	}

	public void setUpper(long upper) {
		this.upper = upper;
	}

	@Override
	public int compareTo(Interval another) {
		if ((another.lower >= this.lower && another.upper <= this.upper) || (another.lower <= this.lower && another.upper >= this.upper)) {
			return 0;
		} else if (another.lower > this.upper) {
			return -1;
		} else {
			return 1;
		}
	}

	public String toString() {
		return "[" + String.valueOf(lower) + "," + String.valueOf(upper) + "]";
	}
}