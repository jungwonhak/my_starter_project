package com.gmail.andreas.ui;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.access.SecuredViewAccessControl;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewLeaveAction;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.gmail.andreas.ui.navigation.NavigationManager;
import com.gmail.andreas.ui.view.admin.product.ProductAdminView;
import com.gmail.andreas.ui.view.admin.user.UserAdminView;
import com.gmail.andreas.ui.view.dashboard.DashboardView;
import com.gmail.andreas.ui.view.storefront.StorefrontView;

/**
 * The main view containing the menu and the content area where actual views are
 * shown.
 * <p>
 * Created as a single View class because the logic is so simple that using a
 * pattern like MVP would add much overhead for little gain. If more complexity
 * is added to the class, you should consider splitting out a presenter.
 */
@SpringViewDisplay
@UIScope
public class MainView extends HorizontalLayout implements ViewDisplay {

	protected CssLayout naviBarContainer;
	protected CssLayout naviBar;
	protected Label logo;
	protected Label activeViewName;
	protected Button menuButton;
	protected CssLayout menu;
	protected Button storefront;
	protected Button dashboard;
	protected Button users;
	protected Button products;
	protected Button logout;
	protected VerticalLayout content;

	private final Map<Class<? extends View>, Button> navigationButtons = new HashMap<>();
	private final NavigationManager navigationManager;
	private final SecuredViewAccessControl viewAccessControl;

	@Autowired
	public MainView(NavigationManager navigationManager, SecuredViewAccessControl viewAccessControl) {
		this.navigationManager = navigationManager;
		this.viewAccessControl = viewAccessControl;
	}

	@PostConstruct
	public void init() {
		setUp();

		attachNavigation(storefront, StorefrontView.class);
		attachNavigation(dashboard, DashboardView.class);
		attachNavigation(users, UserAdminView.class);
		attachNavigation(products, ProductAdminView.class);

		logout.addClickListener(e -> logout());
	}

	public void setUp() {
		naviBarContainer = new CssLayout();
		naviBarContainer.setStyleName("navigation-bar-container");
		naviBarContainer.setWidth("200px");
		naviBarContainer.setHeight("100%");

		naviBar = new CssLayout();
		naviBar.setStyleName("navigation-bar");
		naviBar.setWidth("100%");
		naviBar.setHeight("100%");

		logo = new Label("Bakery");
		logo.setStyleName("logo");
		logo.setWidth("100%");
		logo.setHeight("-1px");

		activeViewName = new Label("Active view");
		activeViewName.setStyleName("activeViewName");
		activeViewName.setWidth("-1px");
		activeViewName.setHeight("-1px");

		menuButton = new Button("menuButton");
		menuButton.setStyleName("menu borderless");
		menuButton.setIcon(VaadinIcons.LINES);
		menuButton.setWidth("-1px");
		menuButton.setHeight("-1px");

		menu = new CssLayout();
		menu.setStyleName("navigation");
		menu.setWidth("100%");
		menu.setHeight("-1px");

		storefront = new Button("Storefront");
		storefront.setStyleName("borderless");
		storefront.setIcon(VaadinIcons.CART_O);
		storefront.setWidth("100%");
		storefront.setHeight("80px");

		dashboard = new Button("Dashboard");
		dashboard.setStyleName("borderless");
		dashboard.setIcon(VaadinIcons.CHART);
		dashboard.setWidth("100%");
		dashboard.setHeight("-1px");

		users = new Button("Users");
		users.setStyleName("borderless");
		users.setIcon(VaadinIcons.USERS);
		users.setWidth("100%");
		users.setHeight("-1px");

		products = new Button("Products");
		products.setStyleName("borderless");
		products.setIcon(VaadinIcons.SHOP);
		products.setWidth("100%");
		products.setHeight("-1px");

		logout = new Button("Log out");
		logout.setStyleName("borderless");
		logout.setIcon(VaadinIcons.SIGN_OUT);
		logout.setWidth("100%");
		logout.setHeight("80px");

		content = new VerticalLayout();
		content.setStyleName("content-container v-scrollable");
		content.setWidth("100.5%");
		content.setHeight("100%");

		menu.addComponents(storefront, dashboard, users, products, logout);
		naviBar.addComponents(logo, activeViewName, menuButton, menu);
		naviBarContainer.addComponent(naviBar);
		addComponents(naviBarContainer, content);

		setStyleName("my_app-shell");
		setWidth("100%");
		setHeight("100%");
		setMargin(false);
		setSpacing(false);
		setExpandRatio(content, 1);
	}

	/**
	 * Makes clicking the given button navigate to the given view if the user
	 * has access to the view.
	 * <p>
	 * If the user does not have access to the view, hides the button.
	 *
	 * @param navigationButton
	 *            the button to use for navigatio
	 * @param targetView
	 *            the view to navigate to when the user clicks the button
	 */
	private void attachNavigation(Button navigationButton, Class<? extends View> targetView) {
		boolean hasAccessToView = viewAccessControl.isAccessGranted(targetView);
		navigationButton.setVisible(hasAccessToView);

		if (hasAccessToView) {
			navigationButtons.put(targetView, navigationButton);
			navigationButton.addClickListener(e -> navigationManager.navigateTo(targetView));
		}
	}

	@Override
	public void showView(View view) {
		content.removeAllComponents();
		content.addComponent(view.getViewComponent());

		navigationButtons.forEach((viewClass, button) -> button.setStyleName("selected", viewClass == view.getClass()));

		Button menuItem = navigationButtons.get(view.getClass());
		String viewName = "";
		if (menuItem != null) {
			viewName = menuItem.getCaption();
		}
		activeViewName.setValue(viewName);
	}

	/**
	 * Logs the user out after ensuring the currently open view has no unsaved
	 * changes.
	 */
	public void logout() {
		ViewLeaveAction doLogout = () -> {
			UI ui = getUI();
			ui.getSession().getSession().invalidate();
			ui.getPage().reload();
		};

		navigationManager.runAfterLeaveConfirmation(doLogout);
	}

}
