package pojo;

import javax.persistence.*;
import java.util.Objects;


@Entity
public class Orderitems {
	private int id;
	private long pid;
	private double cnt;
	private String status;
	private String more;
	private Orders order;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "pid", nullable = false)
	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	@Basic
	@Column(name = "cnt", nullable = false)
	public double getCnt() {
		return cnt;
	}

	public void setCnt(double cnt) {
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

	public Orders getOrder() { return order; }

	public void setOrder(Orders order) { this.order = order; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Orderitems that = (Orderitems) o;
		return id == that.id &&
				pid == that.pid &&
				cnt == that.cnt &&
				Objects.equals(status, that.status) &&
				Objects.equals(more, that.more);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, pid, cnt, status, more);
	}
}
