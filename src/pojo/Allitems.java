package pojo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Allitems {
	private long pid;
	private String res;
	private String name;
	private String type;
	private String units;
	private double cnt;

	@Id
	@Column(name = "pid", nullable = false)
	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	@Basic
	@Column(name = "res", nullable = true, length = 12)
	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}

	@Basic
	@Column(name = "name", nullable = true, length = 12)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "type", nullable = true, length = 12)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Basic
	@Column(name = "units", nullable = true, length = 8)
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	@Basic
	@Column(name = "cnt", nullable = true)
	public double getCnt() {
		return cnt;
	}

	public void setCnt(double cnt) {
		this.cnt = cnt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Allitems allitems = (Allitems) o;
		return pid == allitems.pid &&
				Objects.equals(res, allitems.res) &&
				Objects.equals(name, allitems.name) &&
				Objects.equals(type, allitems.type) &&
				Objects.equals(units, allitems.units) &&
				Objects.equals(cnt, allitems.cnt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pid, res, name, type, units, cnt);
	}
}
