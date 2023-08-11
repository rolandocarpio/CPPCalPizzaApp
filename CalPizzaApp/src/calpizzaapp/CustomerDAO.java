package calpizzaapp;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class CustomerDAO {
	private final SessionFactory sessionFactory;

	public CustomerDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public boolean addCustomer(Customer customer) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Transaction transaction = session.beginTransaction();

			try {
				session.save(customer);
				transaction.commit();
				return true; // Customer added successfully
			} catch (Exception e) {
				transaction.rollback();
				e.printStackTrace();
				return false; // Failed to add customer
			}
		}
	}

	public Customer getCustomerById(int id) {
		try (Session session = sessionFactory.openSession()) {
			return session.get(Customer.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}

	public Customer searchCustomer(String firstName) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Customer> criteriaQuery = builder.createQuery(Customer.class);
			Root<Customer> root = criteriaQuery.from(Customer.class);

			criteriaQuery.where(builder.equal(root.get("firstName"), firstName));

			List<Customer> foundCustomers = session.createQuery(criteriaQuery).getResultList();

			if (!foundCustomers.isEmpty()) {
				return foundCustomers.get(0); // Return the first customer found
			} else {
				return null; // No matching customer found
			}
		}
	}

	public void updateCustomer(Customer customer) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			session.update(customer);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	public void deleteCustomer(Customer customer) {
		try (Session session = sessionFactory.openSession()) {
			Transaction transaction = session.beginTransaction();
			session.delete(customer);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	public List<Customer> listCustomers() {
		try (Session session = sessionFactory.openSession()) {
			String queryString = "FROM Customer";
			Query<Customer> query = session.createQuery(queryString, Customer.class);
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}

	public List<Customer> searchCustomers(String filter) {
		try (Session session = sessionFactory.openSession()) {
			String queryString = "FROM Customer WHERE firstName LIKE :filter OR lastName LIKE :filter";
			Query<Customer> query = session.createQuery(queryString, Customer.class);
			query.setParameter("filter", "%" + filter + "%");
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
			return null;
		}
	}
}