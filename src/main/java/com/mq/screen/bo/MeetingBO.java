package com.mq.screen.bo;

public class MeetingBO {

	private Meeting pre;
	private Meeting current;
	private Meeting next;
	private boolean fresh;//更新标记

	public Meeting getPre() {
		return pre;
	}

	public void setPre(Meeting pre) {
		this.pre = pre;
	}

	public Meeting getCurrent() {
		return current;
	}

	public void setCurrent(Meeting current) {
		this.current = current;
	}

	public Meeting getNext() {
		return next;
	}

	public void setNext(Meeting next) {
		this.next = next;
	}

	public boolean isFresh() {
		return fresh;
	}

	public void setFresh(boolean fresh) {
		this.fresh = fresh;
	}

}
