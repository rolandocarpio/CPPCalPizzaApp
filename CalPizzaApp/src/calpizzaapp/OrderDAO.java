package calpizzaapp;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class OrderDAO {
	private final SessionFactory sessionFactory;

	public OrderDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public boolean addOrder(Order order) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();
			try {
				session.save(order);
				transaction.commit();
				return true; // Return true for success
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
				return false; // Return false for failure
			}
		}
	}

	public boolean updateOrder(Order order) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();
			try {
				session.update(order); // Use update instead of save
				transaction.commit();
				return true; // Return true for success
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
				return false; // Return false for failure
			}
		}
	}

	public boolean deleteOrder(Order order) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();
			try {
				session.delete(order);
				transaction.commit();
				return true; // Return true for success
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
				return false; // Return false for failure
			}
		}
	}

	public Order getOrderById(int order_number) {
		try (Session session = sessionFactory.openSession()) {
			return session.get(Order.class, order_number);
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}

	public List<Order> listOrders() {
		try (Session session = sessionFactory.openSession()) {
			String queryString = "FROM Order";
			Query<Order> query = session.createQuery(queryString, Order.class);
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}

	public List<Order> searchOrders(String filter) {
		try (Session session = sessionFactory.openSession()) {
			String queryString = "FROM Order WHERE orderNumber LIKE :filter";
			Query<Order> query = session.createQuery(queryString, Order.class);
			query.setParameter("filter", "%" + filter + "%");
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}
}