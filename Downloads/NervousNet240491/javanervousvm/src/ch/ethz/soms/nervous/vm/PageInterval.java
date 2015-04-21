package ch.ethz.soms.nervous.vm;
import java.io.Serializable;

public class PageInterval implements Comparable<PageInterval>, Serializable {

	private static final long serialVersionUID = -3883324724432537835L;

	private long pageNumber;
	private Interval interval;

	public long getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	public PageInterval(Interval interval, long pageNumber) {
		this.interval = interval;
		this.pageNumber = pageNumber;
	}

	public String toString() {
		return interval.toString() + "->(" + Long.toHexString(pageNumber) + ")";
	}

	@Override
	public int compareTo(PageInterval o) {
		if (this.pageNumber == -1) {
			return this.interval.compareTo(o.interval);
		} else {
			if (this.pageNumber > o.pageNumber) {
				return 1;
			} else if (this.pageNumber < o.pageNumber) {
				return -1;
			} else {
				return 0;
			}
		}
	}

}
