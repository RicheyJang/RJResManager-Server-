package pojo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Newitems {
	private int id;
	private Integer pid;
	private Byte isNew;
	private Double cnt;
	private String res;
	private String name;
	private String type;
	private String units;
	private String status;
	private String more;
	private Orders order;

	public Orders getOrder() {
		return order;
	}

	public void setOrder(Orders order) {
		this.order = order;
	}

	@Id
	@Column(name = "id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "pid")
	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	@Basic
	@Column(name = "isNew")
	public Byte getIsNew() {
		return isNew;
	}

	public void setIsNew(Byte isNew) {
		this.isNew = isNew;
	}

	@Basic
	@Column(name = "cnt")
	public Double getCnt() {
		return cnt;
	}

	public void setCnt(Double cnt) {
		this.cnt = cnt;
	}

	@Basic
	@Column(name = "res")
	public String getRes() {
		return res;
	}

	public void setRes(String res) {
		this.res = res;
	}

	@Basic
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Basic
	@Column(name = "units")
	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	@Basic
	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Basic
	@Column(name = "more")
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
		Newitems newitems = (Newitems) o;
		return id == newitems.id &&
				Objects.equals(pid, newitems.pid) &&
				Objects.equals(isNew, newitems.isNew) &&
				Objects.equals(cnt, newitems.cnt) &&
				Objects.equals(res, newitems.res) &&
				Objects.equals(name, newitems.name) &&
				Objects.equals(type, newitems.type) &&
				Objects.equals(units, newitems.units) &&
				Objects.equals(status, newitems.status) &&
				Objects.equals(more, newitems.more);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, pid, isNew, cnt, res, name, type, units, status, more);
	}
}
