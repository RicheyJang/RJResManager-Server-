package pojo;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

public class Orders {
	private int id;
	private String workshop;
	private String useclass;
	private Date starttime;
	private Timestamp usetime;
	private Timestamp outtime;
	private Timestamp backtime;
	private String more;
	private String teacher;
	private String header;
	private String admin;
	private String keeper;
	private String accountant;
	private String status;
	private Set<Orderitems> items;
	private Set<Newitems> newItems;

	@Id
	@Column(name = "id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<Orderitems> getItems() { return items; }

	public void setItems(Set<Orderitems> items) { this.items = items; }

	public Set<Newitems> getNewItems() {
		return newItems;
	}

	public void setNewItems(Set<Newitems> newItems) {
		this.newItems = newItems;
	}

	@Basic
	@Column(name = "workshop", nullable = true, length = 12)
	public String getWorkshop() {
		return workshop;
	}

	public void setWorkshop(String workshop) {
		this.workshop = workshop;
	}

	@Basic
	@Column(name = "useclass", nullable = true, length = 15)
	public String getUseclass() {
		return useclass;
	}

	public void setUseclass(String useclass) {
		this.useclass = useclass;
	}

	@Basic
	@Column(name = "starttime", nullable = true)
	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	@Basic
	@Column(name = "usetime", nullable = true)
	public Timestamp getUsetime() {
		return usetime;
	}

	public void setUsetime(Timestamp usetime) {
		this.usetime = usetime;
	}

	@Basic
	@Column(name = "backtime", nullable = true)
	public Timestamp getBacktime() { return backtime; }

	public void setBacktime(Timestamp backtime) { this.backtime = backtime; }

	@Basic
	@Column(name = "outtime", nullable = true)
	public Timestamp getOuttime() { return outtime; }

	public void setOuttime(Timestamp outtime) { this.outtime = outtime; }

	@Basic
	@Column(name = "more", nullable = true, length = 50)
	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}

	@Basic
	@Column(name = "teacher", nullable = true, length = 8)
	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	@Basic
	@Column(name = "header", nullable = true, length = 8)
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Basic
	@Column(name = "admin", nullable = true, length = 8)
	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	@Basic
	@Column(name = "keeper", nullable = true, length = 8)
	public String getKeeper() {
		return keeper;
	}

	public void setKeeper(String keeper) {
		this.keeper = keeper;
	}

	@Basic
	@Column(name = "accountant", nullable = true, length = 8)
	public String getAccountant() {
		return accountant;
	}

	public void setAccountant(String accountant) {
		this.accountant = accountant;
	}

	@Basic
	@Column(name = "status", nullable = true, length = 8)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Orders orders = (Orders) o;
		return id == orders.id &&
				Objects.equals(workshop, orders.workshop) &&
				Objects.equals(useclass, orders.useclass) &&
				Objects.equals(starttime, orders.starttime) &&
				Objects.equals(usetime, orders.usetime) &&
				Objects.equals(more, orders.more) &&
				Objects.equals(teacher, orders.teacher) &&
				Objects.equals(header, orders.header) &&
				Objects.equals(admin, orders.admin) &&
				Objects.equals(keeper, orders.keeper) &&
				Objects.equals(accountant, orders.accountant) &&
				Objects.equals(status, orders.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, workshop, useclass, starttime, usetime, more, teacher, header, admin, keeper, accountant, status);
	}
}
