module CalPizzaApp {

	requires net.bytebuddy;
	requires java.persistence;
	requires transitive javafx.graphics;
	requires transitive org.hibernate.orm.core;
	requires javafx.controls;
	requires java.sql;

	exports calpizzaapp;
}