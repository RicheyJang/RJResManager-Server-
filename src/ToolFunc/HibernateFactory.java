package ToolFunc;

import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.mysql.jdbc.*;
import javax.persistence.metamodel.EntityType;

import java.util.Map;

public class HibernateFactory {
	private static final SessionFactory ourSessionFactory;

	static {
		try {
			Configuration configuration = new Configuration().configure();
			configuration.setProperty("hibernate.connection.username",Config.getConfig().rootName);
			configuration.setProperty("hibernate.connection.password",Config.getConfig().rootPassword);
			ourSessionFactory = configuration.buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Session getSession() throws HibernateException {
		return ourSessionFactory.openSession();
	}
	/*
	public static void main(final String[] args) throws Exception {
		final Session session = getSession();
		try {
			System.out.println("querying all the managed entities...");
			final Metamodel metamodel = session.getSessionFactory().getMetamodel();
			for (EntityType<?> entityType : metamodel.getEntities()) {
				final String entityName = entityType.getName();
				final Query query = session.createQuery("from " + entityName);
				System.out.println("executing: " + query.getQueryString());
				for (Object o : query.list()) {
					System.out.println("  " + o);
				}
			}
		} finally {
			session.close();
		}
	}*/
}