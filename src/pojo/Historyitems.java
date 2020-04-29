package pojo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Historyitems {
	private int orderid;
	private int pid;
	private int cnt;
	private String status;
	private String more;

	@Id
	@Column(name = "orderid", nullable = false)
	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	@Basic
	@Column(name = "pid", nullable = false)
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	@Basic
	@Column(name = "cnt", nullable = false)
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	@Basic
	@Column(name = "status", nullable = true, length = 7)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Basic
	@Column(name = "more", nullable = true, length = 30)
	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Historyitems that = (Historyitems) o;
		return orderid == that.orderid &&
				pid == that.pid &&
				cnt == that.cnt &&
				Objects.equals(status, that.status) &&
				Objects.equals(more, that.more);
	}

	@Override
	public int hashCode() {
		return Objects.hash(orderid, pid, cnt, status, more);
	}
}
