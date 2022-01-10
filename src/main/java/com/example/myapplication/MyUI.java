package com.example.myapplication;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueContext;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("valo")
public class MyUI extends UI {
	
	final VerticalLayout verticalLayout = new VerticalLayout();
	final Grid<Patient> patientGrid = new Grid<Patient>();
	final Grid<Doctor> doctorGrid = new Grid<Doctor>();
	final Grid<Recipe> recipeGrid = new Grid<Recipe>();
	HeaderRow filterRow = recipeGrid.appendHeaderRow();
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        
    	//Загрузка драйвера
    	DAO.loadDriverDB();
    	
    	//Создание БД
        try {
			DAO.createDB(this.getClass().getClassLoader().getResource("/createDB.sql").getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
        MenuBar mainMenu = showMainMenu();
        
        verticalLayout.addComponents(mainMenu);
        verticalLayout.setComponentAlignment(mainMenu, Alignment.TOP_CENTER);

        setContent(verticalLayout);
        
    }
    
    //Функция вывода главного меню
    public MenuBar showMainMenu() {
    	MenuBar mainMenu = new MenuBar();
        MenuItem listsMenu = mainMenu.addItem("Списки");
        
        //Кнопка меню выхода из приложения
        MenuItem exit = mainMenu.addItem("Выход", new MenuBar.Command() {
			
        	//Выход из приложения
			@Override
			public void menuSelected(MenuItem selectedItem) {
				try {
					DAO.shutdown();
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
        
        //Кнопка вывода списка пациентов
        MenuItem listPatientsItem = listsMenu.addItem("Пациенты", new MenuBar.Command() {

			@Override
			public void menuSelected(MenuItem selectedItem) {
				//Получение списка пациентов
				PatientDAO patientDAO = new PatientDAO();
		    	List<Patient> listsPatients = new ArrayList<Patient>();
		    	try {
					listsPatients = patientDAO.getAll();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				verticalLayout.removeAllComponents();
				verticalLayout.addComponent(mainMenu);
				verticalLayout.setComponentAlignment(mainMenu, Alignment.TOP_CENTER);
				verticalLayout.addComponent(showListPatient(listsPatients));
				
				final HorizontalLayout horizontalLayout = new HorizontalLayout();
				
				//Кнопка добавления нового пациента
				Button buttonAdd = new Button("Добавить");
				buttonAdd.addClickListener(e -> {
					getWindowAddPatient();
				});
				
				//Кнопка удаления пациента
				Button buttonDel = new Button("Удалить");
				buttonDel.addClickListener(e -> {
					deletePatient();
				});
				
				Button buttonEdit = new Button("Изменить");
				buttonEdit.addClickListener(e -> {
					getWindowEditPatient();
				});
				
				horizontalLayout.addComponent(buttonAdd);
				horizontalLayout.addComponent(buttonDel);
				horizontalLayout.addComponent(buttonEdit);
				
				verticalLayout.addComponent(horizontalLayout);
			}
		});
        
        //Кнопка вывода списка врачей
        MenuItem listsDoctors = listsMenu.addItem("Доктора", new MenuBar.Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				//Получение списка врачей
				List<Doctor> listsDoctors = new ArrayList<Doctor>();
				DoctorDAO doctorDAO = new DoctorDAO();
		    	try {
					listsDoctors = doctorDAO.getAll();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				verticalLayout.removeAllComponents();
				verticalLayout.addComponent(mainMenu);
				verticalLayout.setComponentAlignment(mainMenu, Alignment.TOP_CENTER);
				verticalLayout.addComponent(showListDoctor(listsDoctors));
				
				final HorizontalLayout horizontalLayout = new HorizontalLayout();
				
				//Кнопка добавления нового доктора
				Button buttonAdd = new Button("Добавить");
				buttonAdd.addClickListener(e -> {
					getWindowAddDoctor();
				});
				
				//Кнопка удаления доктора
				Button buttonDel = new Button("Удалить");
				buttonDel.addClickListener(e -> {
					deleteDoctor();
				});
				
				//Кнопка изменения данных доктора
				Button buttonEdit = new Button("Изменить");
				buttonEdit.addClickListener(e -> {
					getWindowEditDoctor();
				});
				
				//Кнопка покза статистики по докторам
				Button buttonStatistics = new Button("Показать статистику");
				buttonStatistics.addListener(e -> {
					//Получение статистики по врачам
					List<StatisticsDoctor> listsStatisticsDoctors = new ArrayList<StatisticsDoctor>();
					listsStatisticsDoctors = doctorDAO.getDoctorStatistics();
					getWindowStatistics(listsStatisticsDoctors);
				});
				
				horizontalLayout.addComponent(buttonAdd);
				horizontalLayout.addComponent(buttonDel);
				horizontalLayout.addComponent(buttonEdit);
				horizontalLayout.addComponent(buttonStatistics);
				
				verticalLayout.addComponent(horizontalLayout);
			}
		});
        
        //Кнопка вывода списка рецептов
        MenuItem listsRecipes = listsMenu.addItem("Рецепты", new MenuBar.Command() {
			
			@Override
			public void menuSelected(MenuItem selectedItem) {
				//Получение списка пациентов
				RecipeDAO recipeDAO = new RecipeDAO();
		    	List<Recipe> listsRecipes = new ArrayList<Recipe>();
		    	try {
					listsRecipes = recipeDAO.getAll();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				verticalLayout.removeAllComponents();
				verticalLayout.addComponent(mainMenu);
				verticalLayout.setComponentAlignment(mainMenu, Alignment.TOP_CENTER);
				verticalLayout.addComponent(showListRecipe(listsRecipes));
				
				final HorizontalLayout horizontalLayout = new HorizontalLayout();
				
				//Кнопка добавления нового рецепта
				Button buttonAdd = new Button("Добавить");
				buttonAdd.addClickListener(e -> {
					try {
						getWindowAddRecipe();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				});
				
				//Кнопка удаления рецепта
				Button buttonDel = new Button("Удалить");
				buttonDel.addClickListener(e -> {
					deleteRecipe();
				});
				
				Button buttonEdit = new Button("Изменить");
				buttonEdit.addClickListener(e -> {
					try {
						getWindowEditRecipe();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				});
				
				horizontalLayout.addComponent(buttonAdd);
				horizontalLayout.addComponent(buttonDel);
				horizontalLayout.addComponent(buttonEdit);
				
				verticalLayout.addComponent(horizontalLayout);
			}
		});
        
        return mainMenu;
    }
    
   //Функция вывода списка пациентов
    public Grid showListPatient(List listPatients) {
    	patientGrid.setItems(listPatients);
    	patientGrid.removeAllColumns();
    	patientGrid.setSizeFull();
    	patientGrid.setSelectionMode(SelectionMode.SINGLE);
    	patientGrid.addColumn(Patient::getIdPatient).setId("id").setCaption("№");
    	patientGrid.addColumn(Patient::getName).setId("nam_pat").setCaption("Имя");
    	patientGrid.addColumn(Patient::getSurname).setId("surname_pat").setCaption("Фамилия");
    	patientGrid.addColumn(Patient::getMiddleName).setId("middle_name_pat").setCaption("Отчество");
    	patientGrid.addColumn(Patient::getPhoneNumber).setId("phone_number_pat").setCaption("Номер телефона");
        
        return patientGrid;
    }
    
    //Функция вызова модального окна для добавления нового пациента
	@SuppressWarnings("deprecation")
	public void getWindowAddPatient() {
    	Window windowAddPat = new Window("Добавление нового пациента");
    	VerticalLayout verticalLayoutWindowAddPat = new VerticalLayout();
    	Binder<Patient> newPatientBinder = new Binder<>();
    	
    	Button addNewPatientButton = new Button("Добавить");
    	addNewPatientButton.setEnabled(false);
    	
    	addNewPatientButton.addClickListener(e -> {
    		PatientDAO patientDAO = new PatientDAO();
    		try {
    			/*
    			 * Попытка добавить нового пациента
    			 * если успешно, то выведется сообщение
    			 * о успешном добавлении пациента,
    			 * иначе выведется ошибка
    			 */
				if (patientDAO.insert((Patient) newPatientBinder.getBean())) {
					patientDAO.commit();
					Notification insNotification = new Notification("Внимание!");
					insNotification.setDelayMsec(5500);
					insNotification.setPosition(Position.MIDDLE_CENTER);
					insNotification.show("Пацент успечшно добавлен!");
					windowAddPat.close();
					
				} else {
					Notification insNotification = new Notification("ВНИМАНИЕ!"
							,"Ошибка при добавлении пациента, ПАЦИЕНТ НЕ ДОБАВЛЕН!"
							,Notification.TYPE_ERROR_MESSAGE);
					insNotification.setDelayMsec(7500);
					insNotification.setPosition(Position.MIDDLE_CENTER);
					insNotification.show("");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
    	});
    	
		verticalLayoutWindowAddPat.addComponent(new TextField("Имя"));
		verticalLayoutWindowAddPat.addComponent(new TextField("Фамилия"));
		verticalLayoutWindowAddPat.addComponent(new TextField("Отчество"));
		verticalLayoutWindowAddPat.addComponent(new TextField("Номер телефона"));
		verticalLayoutWindowAddPat.addComponent(addNewPatientButton);
		windowAddPat.setContent(verticalLayoutWindowAddPat);
		
		newPatientBinder.forField((TextField) verticalLayoutWindowAddPat.getComponent(0))
			.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 букв")
			.asRequired("Введите имя").bind(Patient::getName, Patient::setName);
		newPatientBinder.forField((TextField) verticalLayoutWindowAddPat.getComponent(1))
			.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 букв")
			.asRequired("Введите фамилию")
			.bind(Patient::getSurname, Patient::setSurname);
		newPatientBinder.forField((TextField) verticalLayoutWindowAddPat.getComponent(2))
			.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 букв")
			.asRequired("Введите отчество")
			.bind(Patient::getMiddleName, Patient::setMiddleName); 
		newPatientBinder.forField((TextField) verticalLayoutWindowAddPat.getComponent(3))
			.withValidator(phoneNumber -> phoneNumber.matches("[0-9]++"), "Может включать только символы: 0-9")
			.asRequired("Введите номер телефона")
			.bind(Patient::getPhoneNumber, Patient::setPhoneNumber);
		newPatientBinder.setBean(new Patient());
		newPatientBinder.addStatusChangeListener(
				e -> addNewPatientButton.setEnabled(newPatientBinder.isValid()));
		
		windowAddPat.center();
		windowAddPat.setModal(true);
		windowAddPat.setResizable(false);
		windowAddPat.setDraggable(false);
		windowAddPat.setWidth("500px");
		
		//Обновление таблицы(списка пациентов) после закрытия модального окна 
		windowAddPat.addCloseListener(eventClose -> {
			PatientDAO patientDAO = new PatientDAO();
			verticalLayout.removeComponent(patientGrid);
			try {
				verticalLayout.addComponent(showListPatient(patientDAO.getAll()), 1);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		
		addWindow(windowAddPat);
    }
    
	//Метод удаления пациента и обновление соответсвующего интерфейса
	@SuppressWarnings("unused")
	public void deletePatient() {
		Set<Patient> selectedPatient = patientGrid.getSelectedItems();
		Iterator<Patient> it = selectedPatient.iterator();
		//Проверка есть ли пациент на удаление
		if (!selectedPatient.isEmpty()) {
			Patient delPatient = it.next();
			PatientDAO delPatientDAO = new PatientDAO();
			/*
			 * Если удаление прошло успешно, то соообщить пользователю об этом,
			 * иначе сообщить об ошибке
			 */
			if(delPatientDAO.delete(delPatient.getIdPatient())) {
				//Подтверждение транзакции, сохранение изменений в базе
				try {
					delPatientDAO.commit();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				//Вывод сообщение об успешном удалении
				Notification delNotification = new Notification("Внимание!");
				delNotification.setDelayMsec(5500);
				delNotification.setPosition(Position.MIDDLE_CENTER);
				delNotification.show("Пациент с номером: " + delPatient.getIdPatient() + " успешно удалён");
				//Обновление списка пациентов
				verticalLayout.removeComponent(patientGrid);
				try {
					verticalLayout.addComponent(showListPatient(delPatientDAO.getAll()), 1);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				//Вывод сообщения об ошибке при удалении
				Notification delNotification = new Notification("ВНИМАНИЕ!");
				delNotification.setDelayMsec(7500);
				delNotification.setPosition(Position.MIDDLE_CENTER);
				delNotification.show("Ошибка при удалении пациента: " + delPatient.getFIOPatient());
			}
		}
	}
	
	//Функция вызова модального окна для редактирования пациента пациента
	public void getWindowEditPatient() {
		Set<Patient> selectedPatient = patientGrid.getSelectedItems();
		Iterator<Patient> it = selectedPatient.iterator();
		if (!selectedPatient.isEmpty()) {
			Window windowEditPatient = new Window("Редактирование пациента");
			VerticalLayout verticalLayoutWindowEditPat = new VerticalLayout();
			HorizontalLayout horizontalLayoutWindowEditPat = new HorizontalLayout();
			Binder<Patient> editPatientBinder = new Binder<>();
			Patient editPatient = it.next();
			//Сохранение дубликата, в случае отмены изменений
			Patient editPatientReset = new Patient(editPatient.getIdPatient(), editPatient.getName(), editPatient.getSurname(),
					editPatient.getMiddleName(), editPatient.getPhoneNumber());
			
			Button okButton = new Button("ОК");
			Button cancelButton = new Button("Отмена");
			okButton.addListener(event -> {
				PatientDAO editPatientDAO = new PatientDAO();
				
				//Заптись в editPatient'а новых данных
				try {
					editPatientBinder.writeBean(editPatient);
				} catch (ValidationException e1) {
					e1.printStackTrace();
				}
				
				try {
					editPatientDAO.update(editPatient);
					editPatientDAO.commit();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			});
			
			/*
			 * Кнопка отмены, перезапись в БД данных о пользователе,
			 * данные которого изменяли
			 */
			cancelButton.addClickListener(event -> {
				try {
					PatientDAO editPatientDAORollback = new PatientDAO();
					editPatientDAORollback.update(editPatientReset);
					editPatientDAORollback.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
			
			verticalLayoutWindowEditPat.addComponent(new TextField("Имя"));
			verticalLayoutWindowEditPat.addComponent(new TextField("Фамилия"));
			verticalLayoutWindowEditPat.addComponent(new TextField("Отчество"));
			verticalLayoutWindowEditPat.addComponent(new TextField("Номер телефона"));
			horizontalLayoutWindowEditPat.addComponent(okButton);
			horizontalLayoutWindowEditPat.addComponent(cancelButton);
			verticalLayoutWindowEditPat.addComponent(horizontalLayoutWindowEditPat);
			windowEditPatient.setContent(verticalLayoutWindowEditPat);
			
			editPatientBinder.forField((TextField) verticalLayoutWindowEditPat.getComponent(0))
				.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 букв")
				.bind(Patient::getName, Patient::setName);
			editPatientBinder.forField((TextField) verticalLayoutWindowEditPat.getComponent(1))
				.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 букв")
				.bind(Patient::getSurname, Patient::setSurname);
			editPatientBinder.forField((TextField) verticalLayoutWindowEditPat.getComponent(2))
				.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 букв")
				.bind(Patient::getMiddleName, Patient::setMiddleName); 
			editPatientBinder.forField((TextField) verticalLayoutWindowEditPat.getComponent(3))
				.withValidator(phoneNumber -> phoneNumber.matches("[0-9]++"), "Может включать символы: 0-9")
				.bind(Patient::getPhoneNumber, Patient::setPhoneNumber);
			editPatientBinder.readBean(editPatient);
			
			windowEditPatient.center();
			windowEditPatient.setModal(true);
			windowEditPatient.setResizable(false);
			windowEditPatient.setDraggable(false);
			windowEditPatient.setWidth("500px");
			
			
			windowEditPatient.addCloseListener(event -> {
				PatientDAO patientDAO = new PatientDAO();
				verticalLayout.removeComponent(patientGrid);
				try {
					verticalLayout.addComponent(showListPatient(patientDAO.getAll()), 1);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			});
			
			addWindow(windowEditPatient);
		}
	}
	
	
	
	//Функция вывода списка врачей
    public Grid showListDoctor(List listDoctor) {
    	doctorGrid.setItems(listDoctor);
    	doctorGrid.removeAllColumns();
    	doctorGrid.setSizeFull();
    	doctorGrid.setSelectionMode(SelectionMode.SINGLE);
    	doctorGrid.addColumn(Doctor::getIdDoctor).setId("id").setCaption("№");
    	doctorGrid.addColumn(Doctor::getName).setId("nam_pat").setCaption("Имя");
    	doctorGrid.addColumn(Doctor::getSurname).setId("surname_pat").setCaption("Фамилия");
    	doctorGrid.addColumn(Doctor::getMiddleName).setId("middle_name_pat").setCaption("Отчество");
    	doctorGrid.addColumn(Doctor::getSpecialty).setId("specialty_pat").setCaption("Специальность");
        
        return doctorGrid;
    }
	
    //Функция вызова модального окна для добавления нового пациента
    public void getWindowAddDoctor() {
    	Window windowAddDoc = new Window("Добавление нового доктора");
    	VerticalLayout verticalLayoutWindowAddDoc = new VerticalLayout();
    	Binder<Doctor> newDoctorBinder = new Binder<>();
    	
    	Button addNewDoctorButton = new Button("Добавить");
    	addNewDoctorButton.setEnabled(false);
    	
    	addNewDoctorButton.addClickListener(e -> {
    		DoctorDAO doctorDAO = new DoctorDAO();
    		try {
    			/*
    			 * Попытка добавить нового доктора
    			 * если успешно, то выведется сообщение
    			 * о успешном добавлении доктора,
    			 * иначе выведется ошибка
    			 */
				if (doctorDAO.insert((Doctor) newDoctorBinder.getBean())) {
					doctorDAO.commit();
					Notification insNotification = new Notification("Внимание!");
					insNotification.setDelayMsec(5500);
					insNotification.setPosition(Position.MIDDLE_CENTER);
					insNotification.show("Доктор успечшно добавлен!");
					windowAddDoc.close();
					
				} else {
					Notification insNotification = new Notification("ВНИМАНИЕ!"
							,"Ошибка при добавлении доктора, ДОКТОР НЕ ДОБАВЛЕН!"
							,Notification.TYPE_ERROR_MESSAGE);
					insNotification.setDelayMsec(7500);
					insNotification.setPosition(Position.MIDDLE_CENTER);
					insNotification.show("");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
    	});
    	
		verticalLayoutWindowAddDoc.addComponent(new TextField("Имя"));
		verticalLayoutWindowAddDoc.addComponent(new TextField("Фамилия"));
		verticalLayoutWindowAddDoc.addComponent(new TextField("Отчество"));
		verticalLayoutWindowAddDoc.addComponent(new TextField("Специальность"));
		verticalLayoutWindowAddDoc.addComponent(addNewDoctorButton);
		windowAddDoc.setContent(verticalLayoutWindowAddDoc);
		
		newDoctorBinder.forField((TextField) verticalLayoutWindowAddDoc.getComponent(0))
			.withValidator(name -> name.matches("[а-яА-Я_ё_Ё]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 буквы")
			.asRequired("Введите имя").bind(Doctor::getName, Doctor::setName);
		newDoctorBinder.forField((TextField) verticalLayoutWindowAddDoc.getComponent(1))
			.withValidator(name -> name.matches("[а-яА-Я_ё_Ё]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 буквы")
			.asRequired("Введите фамилию")
			.bind(Doctor::getSurname, Doctor::setSurname);
		newDoctorBinder.forField((TextField) verticalLayoutWindowAddDoc.getComponent(2))
			.withValidator(name -> name.matches("[а-яА-Я_ё_Ё]++") && name.length() > 1,
					"Используются только символы от А - Я и должно состоять не менее чем из 2 буквы")
			.asRequired("Введите отчество")
			.bind(Doctor::getMiddleName, Doctor::setMiddleName); 
		newDoctorBinder.forField((TextField) verticalLayoutWindowAddDoc.getComponent(3))
			.withValidator(spec -> spec.matches("[а-яА-Я_ё_Ё]++") && spec.length() > 3,
					"Используются только символы от А - Я и должно состоять не менее чем из 4 буквы")
			.asRequired("Введите специальность")
			.bind(Doctor::getSpecialty, Doctor::setSpecialty);
		newDoctorBinder.setBean(new Doctor());
		newDoctorBinder.addStatusChangeListener(
				e -> addNewDoctorButton.setEnabled(newDoctorBinder.isValid()));
		
		windowAddDoc.center();
		windowAddDoc.setModal(true);
		windowAddDoc.setResizable(false);
		windowAddDoc.setDraggable(false);
		windowAddDoc.setWidth("500px");
		
		//Обновление таблицы(списка врачей) после закрытия модального окна 
		windowAddDoc.addCloseListener(eventClose -> {
			DoctorDAO doctorDAO = new DoctorDAO();
			verticalLayout.removeComponent(doctorGrid);
			try {
				verticalLayout.addComponent(showListDoctor(doctorDAO.getAll()), 1);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		
		addWindow(windowAddDoc);
    }
	
    //Метод удаления доктра и обновление соответсвующего интерфейса
  	@SuppressWarnings("unused")
  	public void deleteDoctor() {
  		Set<Doctor> selectedDoctor = doctorGrid.getSelectedItems();
  		Iterator<Doctor> it = selectedDoctor.iterator();
  		//Проверка есть ли доктор на удаление
  		if (!selectedDoctor.isEmpty()) {
  			Doctor delDoctor = it.next();
  			DoctorDAO delDoctorDAO = new DoctorDAO();
  			/*
  			 * Если удаление прошло успешно, то соообщить пользователю об этом,
  			 * иначе сообщить об ошибке
  			 */
  			if(delDoctorDAO.delete(delDoctor.getIdDoctor())) {
  				//Подтверждение транзакции, сохранение изменений в базе
  				try {
  					delDoctorDAO.commit();
  				} catch (SQLException e1) {
  					e1.printStackTrace();
  				}
  				//Вывод сообщение об успешном удалении
  				Notification delNotification = new Notification("Внимание!");
  				delNotification.setDelayMsec(5500);
  				delNotification.setPosition(Position.MIDDLE_CENTER);
  				delNotification.show("Доктор с номером: " + delDoctor.getIdDoctor() + " успешно удалён");
  				//Обновление списка докторов
  				verticalLayout.removeComponent(doctorGrid);
  				try {
  					verticalLayout.addComponent(showListDoctor(delDoctorDAO.getAll()), 1);
  				} catch (SQLException e1) {
  					e1.printStackTrace();
  				}
  			} else {
  				//Вывод сообщения об ошибке при удалении
  				Notification delNotification = new Notification("ВНИМАНИЕ!");
  				delNotification.setDelayMsec(7500);
  				delNotification.setPosition(Position.MIDDLE_CENTER);
  				delNotification.show("Ошибка при удалении доктора: " + delDoctor.getFIODoctor());
  			}
  		}
  	}
    
  	//Функция вызова модального окна для редактирования доктора
  	public void getWindowEditDoctor() {
  		Set<Doctor> selectedDoctor = doctorGrid.getSelectedItems();
  		Iterator<Doctor> it = selectedDoctor.iterator();
  		if (!selectedDoctor.isEmpty()) {
  			Window windowEditDoctor = new Window("Редактирование доктора");
  			VerticalLayout verticalLayoutWindowEditDoc = new VerticalLayout();
  			HorizontalLayout horizontalLayoutWindowEditDoc = new HorizontalLayout();
  			Binder<Doctor> editDoctorBinder = new Binder<>();
  			Doctor editDoctor = it.next();
  			//Сохранение дубликата, в случае отмены изменений
  			Doctor editDoctorReset = new Doctor(editDoctor.getIdDoctor(), editDoctor.getName(), editDoctor.getSurname(),
  					editDoctor.getMiddleName(), editDoctor.getSpecialty());
  			
  			Button okButton = new Button("ОК");
  			Button cancelButton = new Button("Отмена");
  			okButton.addListener(event -> {
  				DoctorDAO editDoctorDAO = new DoctorDAO();
  				
  				//Заптись в editDoctor'а новых данных
  				try {
  					editDoctorBinder.writeBean(editDoctor);
  				} catch (ValidationException e1) {
  					e1.printStackTrace();
  				}
  				
  				try {
  					editDoctorDAO.update(editDoctor);
  					editDoctorDAO.commit();
  				} catch (SQLException e1) {
  					e1.printStackTrace();
  				}
  			});
  			
  			/*
  			 * Кнопка отмены, перезапись в БД данных о докторе,
  			 * данные которого изменяли
  			 */
  			cancelButton.addClickListener(event -> {
  				try {
  					DoctorDAO editDoctorDAORollback = new DoctorDAO();
  					editDoctorDAORollback.update(editDoctorReset);
  					editDoctorDAORollback.commit();
  				} catch (SQLException e) {
  					e.printStackTrace();
  				}
  			});
  			
  			verticalLayoutWindowEditDoc.addComponent(new TextField("Имя"));
  			verticalLayoutWindowEditDoc.addComponent(new TextField("Фамилия"));
  			verticalLayoutWindowEditDoc.addComponent(new TextField("Отчество"));
  			verticalLayoutWindowEditDoc.addComponent(new TextField("Специальность"));
  			horizontalLayoutWindowEditDoc.addComponent(okButton);
  			horizontalLayoutWindowEditDoc.addComponent(cancelButton);
  			verticalLayoutWindowEditDoc.addComponent(horizontalLayoutWindowEditDoc);
  			windowEditDoctor.setContent(verticalLayoutWindowEditDoc);
  			
  			editDoctorBinder.forField((TextField) verticalLayoutWindowEditDoc.getComponent(0))
  				.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
  					"Используются только символы от А - Я и должно состоять не менее чем из 2 буквы")
  				.bind(Doctor::getName, Doctor::setName);
  			editDoctorBinder.forField((TextField) verticalLayoutWindowEditDoc.getComponent(1))
  				.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
  					"Используются только символы от А - Я и должно состоять не менее чем из 2 буквы")
  				.bind(Doctor::getSurname, Doctor::setSurname);
  			editDoctorBinder.forField((TextField) verticalLayoutWindowEditDoc.getComponent(2))
  				.withValidator(name -> name.matches("[а-яА-Я]++") && name.length() > 1,
  					"Используются только символы от А - Я и должно состоять не менее чем из 2 буквы")
  				.bind(Doctor::getMiddleName, Doctor::setMiddleName); 
  			editDoctorBinder.forField((TextField) verticalLayoutWindowEditDoc.getComponent(3))
  				.withValidator(spec -> spec.matches("[а-яА-Я]++") && spec.length() > 3,
  						"Используются только символы от А - Я и должно состоять не менее из 4 буквы")
  				.bind(Doctor::getSpecialty, Doctor::setSpecialty);
  			editDoctorBinder.readBean(editDoctor);
  			
  			windowEditDoctor.center();
  			windowEditDoctor.setModal(true);
  			windowEditDoctor.setResizable(false);
  			windowEditDoctor.setDraggable(false);
  			windowEditDoctor.setWidth("500px");
  			
  			
  			windowEditDoctor.addCloseListener(event -> {
  				DoctorDAO doctorDAO = new DoctorDAO();
  				verticalLayout.removeComponent(doctorGrid);
  				try {
  					verticalLayout.addComponent(showListDoctor(doctorDAO.getAll()), 1);
  				} catch (SQLException e1) {
  					e1.printStackTrace();
  				}
  			});
  			
  			addWindow(windowEditDoctor);
  		}
  	}
  	
  	//Функция вывода окна содержащего статистику по врачам
  	public void getWindowStatistics(List listsStatDoctor) {
  		Window windowStatistics = new Window("Статистика докторов");
    	VerticalLayout verticalLayoutWindowStatistics = new VerticalLayout();
    	Grid<StatisticsDoctor> gridStatisticsDoctors = new Grid<StatisticsDoctor>();
    	
    	ListDataProvider<StatisticsDoctor> dataProviderStatDoctor = new ListDataProvider<StatisticsDoctor>(listsStatDoctor);
    	gridStatisticsDoctors.setDataProvider(dataProviderStatDoctor);
    	gridStatisticsDoctors.setSizeFull();
    	gridStatisticsDoctors.setSelectionMode(SelectionMode.NONE);
    	Grid.Column<StatisticsDoctor, String> FIODoctorColumn = gridStatisticsDoctors.addColumn(StatisticsDoctor::getFIO).setId("fio_stat").setCaption("Доктор");
    	gridStatisticsDoctors.addColumn(StatisticsDoctor::getCountPatientTreated).setCaption("Кол-во пациентов на лечении");
    	gridStatisticsDoctors.addColumn(StatisticsDoctor::getCountPatientEndTreated).setCaption("Кол-во пациентов прошедших лечение");
    	gridStatisticsDoctors.addColumn(StatisticsDoctor::getCountPatient).setCaption("Всего пациентов");
    	
    	HeaderRow filterRowStatDoctor = gridStatisticsDoctors.appendHeaderRow();
    	//Создание фильтра для фильтрации докторов в статистике о них
    	TextField filterDoctors = new TextField();
    	filterDoctors.addValueChangeListener(event -> dataProviderStatDoctor.addFilter(
    			doctor -> StringUtils.containsIgnoreCase(doctor.getFIO(), filterDoctors.getValue())));
        filterDoctors.setValueChangeMode(ValueChangeMode.EAGER);
        filterRowStatDoctor.getCell(FIODoctorColumn).setComponent(filterDoctors);
        filterDoctors.setSizeFull();
        filterDoctors.setHeight("25px");
        filterDoctors.setPlaceholder("Фильтр докторов...");
    	
    	Button buttonOk = new Button("ОК");
    	buttonOk.addClickListener(listener -> {
    		windowStatistics.close();
    	});
    	
    	verticalLayoutWindowStatistics.addComponent(gridStatisticsDoctors);
    	verticalLayoutWindowStatistics.addComponent(buttonOk);
    	
    	windowStatistics.center();
    	windowStatistics.setModal(true);
    	windowStatistics.setResizable(false);
    	windowStatistics.setDraggable(false);
    	windowStatistics.setSizeFull();
    	windowStatistics.setContent(verticalLayoutWindowStatistics);
    	
    	addWindow(windowStatistics);
    	
  	}
  	
  	
  	
    //Функция вывода списка рецептов
    public Grid showListRecipe(List listRecipes) {
    	ListDataProvider<Recipe> dateProviderRecipe = new ListDataProvider<Recipe>(listRecipes);
    	recipeGrid.setDataProvider(dateProviderRecipe);
    	recipeGrid.removeHeaderRow(filterRow);
    	recipeGrid.removeAllColumns();
    	recipeGrid.setSizeFull();
    	recipeGrid.setSelectionMode(SelectionMode.SINGLE);
    	recipeGrid.addColumn(Recipe::getIdRecipe).setId("id").setCaption("№");
    	Grid.Column<Recipe, String> descriptionColumn = recipeGrid.addColumn(Recipe::getDescriptions).setId("des_rec").setCaption("Описание");
    	recipeGrid.getColumn("des_rec").setWidth(210.0);
    	Grid.Column<Recipe, String> FIOPatientColumn = recipeGrid.addColumn(Recipe::getFIOPatient).setId("pat_rec").setCaption("Пациент");
    	recipeGrid.addColumn(Recipe::getFIODoctor).setId("doc_pat").setCaption("Доктор");
    	recipeGrid.addColumn(Recipe::getDateCreateLD).setId("date_create_rec").setCaption("Дата создания");
    	recipeGrid.getColumn("date_create_rec").setWidth(170.0);
    	recipeGrid.addColumn(Recipe::getTermLD).setId("term_rec").setCaption("Дата истечения рецепта");
    	recipeGrid.getColumn("term_rec").setWidth(150.0);
    	Grid.Column<Recipe, String> priorityColumn = recipeGrid.addColumn(Recipe::getNamePriority).setId("prior_pat").setCaption("Приоритет");
    	
    	filterRow = recipeGrid.appendHeaderRow();
    	//Создание фильтра описания рецептов
    	TextField filterDescriptions = new TextField();
    	filterDescriptions.addValueChangeListener(event -> dateProviderRecipe.addFilter(
    			recipe -> StringUtils.containsIgnoreCase(recipe.getDescriptions(), filterDescriptions.getValue())));
        filterDescriptions.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(descriptionColumn).setComponent(filterDescriptions);
        filterDescriptions.setSizeFull();
        filterDescriptions.setHeight("25px");
        filterDescriptions.setPlaceholder("Фильтр описания...");
        
        //Создание фильтра по имени пациента
    	TextField filterPatient = new TextField();
    	filterPatient.addValueChangeListener(event -> dateProviderRecipe.addFilter(
    			recipe -> StringUtils.containsIgnoreCase(recipe.getFIOPatient(), filterPatient.getValue())));
        filterPatient.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(FIOPatientColumn).setComponent(filterPatient);
        filterPatient.setSizeFull();
        filterPatient.setHeight("25px");
        filterPatient.setPlaceholder("Фильтр пациентов...");
        
        //Создание фильтра по приоритету
    	TextField filterPriority = new TextField();
    	filterPriority.addValueChangeListener(event -> dateProviderRecipe.addFilter(
    			recipe -> StringUtils.containsIgnoreCase(recipe.getNamePriority(), filterPriority.getValue())));
        filterPriority.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(priorityColumn).setComponent(filterPriority);
        filterPriority.setSizeFull();
        filterPriority.setHeight("25px");
        filterPriority.setPlaceholder("Фильтр приоритетов...");
        
        return recipeGrid;
    }
  	
    //Функция вызова модального окна для добавления нового рецепта
   	@SuppressWarnings("deprecation")
   	public void getWindowAddRecipe() throws SQLException {
       	Window windowAddRec = new Window("Добавление нового рецепта");
       	VerticalLayout verticalLayoutWindowAddRec = new VerticalLayout();
       	Binder<Recipe> newRecipeBinder = new Binder<>();
       	
       	Button addNewRecipeButton = new Button("Добавить");
       	addNewRecipeButton.setEnabled(false);
       	
       	addNewRecipeButton.addClickListener(e -> {
       		RecipeDAO recipeDAO = new RecipeDAO();
       		try {
       			/*
       			 * Попытка добавить нового рецепта
       			 * если успешно, то выведется сообщение
       			 * о успешном добавлении рецепта,
       			 * иначе выведется ошибка
       			 */
   				if (recipeDAO.insert((Recipe) newRecipeBinder.getBean())) {
   					recipeDAO.commit();
   					Notification insNotification = new Notification("Внимание!");
   					insNotification.setDelayMsec(5500);
   					insNotification.setPosition(Position.MIDDLE_CENTER);
   					insNotification.show("Рецепт успечшно добавлен!");
   					windowAddRec.close();
   					
   				} else {
   					Notification insNotification = new Notification("ВНИМАНИЕ!"
   							,"Ошибка при добавлении рецепта, РЕЦЕПТ НЕ ДОБАВЛЕН!"
   							,Notification.TYPE_ERROR_MESSAGE);
   					insNotification.setDelayMsec(7500);
   					insNotification.setPosition(Position.MIDDLE_CENTER);
   					insNotification.show("");
   				}
   			} catch (SQLException e1) {
   				e1.printStackTrace();
   			}
       	});
       	
       	//Создание комбо бокса для выбора пациента из списка
       	PatientDAO patientDAO = new PatientDAO();
       	List<Patient> patientList = patientDAO.getAll();
       	ComboBox<Patient> comboBoxPatient = new ComboBox<Patient>("Выбирите пациента");
       	comboBoxPatient.setItems(patientList);
       	comboBoxPatient.setItemCaptionGenerator(Patient::getFIOPatient);
       	comboBoxPatient.setWidth("300px");
       	//comboBoxPatient.setEmptySelectionAllowed(false);
       	comboBoxPatient.setPlaceholder("Пациент не выбран...");
       	
        //Создание комбо бокса для выбора доктора из списка
       	DoctorDAO doctorDAO = new DoctorDAO();
       	List<Doctor> doctorList = doctorDAO.getAll();
       	ComboBox<Doctor> comboBoxDoctor = new ComboBox<Doctor>("Выбирите доктора");
       	comboBoxDoctor.setItems(doctorList);
       	comboBoxDoctor.setItemCaptionGenerator(Doctor::getFIODoctor);
       	comboBoxDoctor.setWidth("300px");
       	//comboBoxDoctor.setEmptySelectionAllowed(false);
       	comboBoxDoctor.setPlaceholder("Доктор не выбран...");
       	
        //Создание комбо бокса для выбора приоритета из списка
       	PriorityDAO priorityDAO = new PriorityDAO();
       	List<Priority> priorityList = priorityDAO.getAll();
       	ComboBox<Priority> comboBoxPriority = new ComboBox<Priority>("Выбирите приоритет");
       	comboBoxPriority.setItems(priorityList);
       	comboBoxPriority.setItemCaptionGenerator(Priority::getName);
       	comboBoxPriority.setWidth("300px");
       	comboBoxPriority.setEmptySelectionAllowed(false);
       	comboBoxPriority.setPlaceholder("Приоритет не выбран...");
       	
       	//TextArea для заполнения описания рецепта
       	TextArea textAreaDescription = new TextArea("Описание");
       	textAreaDescription.setSizeFull();
       	
       	//Поле для заполнения даты создания
       	DateField dateFieldDateCreate = new DateField("Выбирите дату создания рецепта");
       	dateFieldDateCreate.setLocale(new Locale("ru", "RU"));
       	dateFieldDateCreate.setPlaceholder("Выбирите дату");
       	
        //Поле для заполнения даты истечения
       	DateField dateFieldTerm = new DateField("Выбирите дату истечения рецепта");
       	dateFieldTerm.setLocale(new Locale("ru", "RU"));
       	dateFieldTerm.setPlaceholder("Выбирите дату");
       	
   		verticalLayoutWindowAddRec.addComponent(comboBoxPatient);
   		verticalLayoutWindowAddRec.addComponent(comboBoxDoctor);
   		verticalLayoutWindowAddRec.addComponent(comboBoxPriority);
   		verticalLayoutWindowAddRec.addComponent(dateFieldDateCreate);
   		verticalLayoutWindowAddRec.addComponent(dateFieldTerm);
   		verticalLayoutWindowAddRec.addComponent(textAreaDescription);
   		verticalLayoutWindowAddRec.addComponent(addNewRecipeButton);
   		windowAddRec.setContent(verticalLayoutWindowAddRec);
   		
   		newRecipeBinder.forField(textAreaDescription)
   			.withValidator(name -> name.matches("[а-яА-Я_._,_(_)_ _\n_Ё_ё]++") && name.length() > 4,
   					"Используются только символы от А - Я, следующие знаки: . , ( ) и должно состоять не менее чем из 5 букв")
   			.asRequired("Введите описание рецепта").bind(Recipe::getDescriptions, Recipe::setDescriptions);
   		newRecipeBinder.forField(comboBoxPatient)
   			.withConverter(new ComboBoxPatientToIdConverter())
   			.asRequired("Выбирите пациента")
   			.bind(Recipe::getIdPatient, Recipe::setIdPatient);
   		newRecipeBinder.forField(comboBoxDoctor)
   			.withConverter(new ComboBoxDoctorToIdConverter())
			.asRequired("Выбирите доктора")
			.bind(Recipe::getIdDoctor, Recipe::setIdDoctor);
   		newRecipeBinder.forField(comboBoxPriority)
			.withConverter(new ComboBoxPriorityToIdConverter())
			.asRequired("Выбирите приоритет")
			.bind(Recipe::getIdPriority, Recipe::setIdPriority);
   		newRecipeBinder.forField(dateFieldDateCreate)
   			.withConverter(new localDateToDate())
   			.asRequired("Выбирите дату создания рецепта")
   			.bind(Recipe::getDateCreate, Recipe::setDateCreate);
   		newRecipeBinder.forField(dateFieldTerm)
   			.withConverter(new localDateToDate())
   			.asRequired("Выбирите дату истечения рецепта")
   			.bind(Recipe::getTerm, Recipe::setTerm);

   		newRecipeBinder.setBean(new Recipe());
   		newRecipeBinder.addStatusChangeListener(e -> {
   			if (!textAreaDescription.isEmpty() && !comboBoxDoctor.isEmpty()
   	   				&& !comboBoxPatient.isEmpty() && !comboBoxPriority.isEmpty()
   	   				&& !dateFieldDateCreate.isEmpty() && !dateFieldTerm.isEmpty())
   	   			addNewRecipeButton.setEnabled(true);
   		});
   		
   		windowAddRec.center();
   		windowAddRec.setModal(true);
   		windowAddRec.setResizable(false);
   		windowAddRec.setDraggable(false);
   		windowAddRec.setWidth("500px");
   		
   		//Обновление таблицы(списка рецептов) после закрытия модального окна 
   		windowAddRec.addCloseListener(eventClose -> {
   			RecipeDAO recipeDAO = new RecipeDAO();
   			verticalLayout.removeComponent(recipeGrid);
   			try {
   				verticalLayout.addComponent(showListRecipe(recipeDAO.getAll()), 1);
   			} catch (SQLException e1) {
   				e1.printStackTrace();
   			}
   		});
   		
   		addWindow(windowAddRec);
       }
    
    //Метод удаления рецепта и обновление соответсвующего интерфейса
  	@SuppressWarnings("unused")
  	public void deleteRecipe() {
  		Set<Recipe> selectedRecipe = recipeGrid.getSelectedItems();
  		Iterator<Recipe> it = selectedRecipe.iterator();
  		//Проверка есть ли пациент на удаление
  		if (!selectedRecipe.isEmpty()) {
  			Recipe delRecipe = it.next();
  			RecipeDAO delRecipeDAO = new RecipeDAO();
  			/*
  			 * Если удаление прошло успешно, то соообщить пользователю об этом,
  			 * иначе сообщить об ошибке
  			 */
  			if(delRecipeDAO.delete(delRecipe.getIdRecipe())) {
  				//Подтверждение транзакции, сохранение изменений в базе
  				try {
  					delRecipeDAO.commit();
  				} catch (SQLException e1) {
  					e1.printStackTrace();
  				}
  				//Вывод сообщение об успешном удалении
  				Notification delNotification = new Notification("Внимание!");
  				delNotification.setDelayMsec(5500);
  				delNotification.setPosition(Position.MIDDLE_CENTER);
  				delNotification.show("Рецепт с номером: " + delRecipe.getIdPatient() + " успешно удалён");
  				//Обновление списка рецептов
  				verticalLayout.removeComponent(recipeGrid);
  				try {
  					verticalLayout.addComponent(showListRecipe(delRecipeDAO.getAll()), 1);
  				} catch (SQLException e1) {
  					e1.printStackTrace();
  				}
  			} else {
  				//Вывод сообщения об ошибке при удалении
  				Notification delNotification = new Notification("ВНИМАНИЕ!", "Ошибка при удалении!", Notification.TYPE_ERROR_MESSAGE);
  				delNotification.setDelayMsec(7500);
  				delNotification.setPosition(Position.MIDDLE_CENTER);
  				delNotification.show("");
  			}
  		}
  	}
   	
   //Функция вызова модального окна для редактирования рецепта
  	public void getWindowEditRecipe() throws SQLException {
  		Set<Recipe> selectedRecipe = recipeGrid.getSelectedItems();
  		Iterator<Recipe> it = selectedRecipe.iterator();
  		if (!selectedRecipe.isEmpty()) {
  			Window windowEditRecipe = new Window("Редактирование рецепта");
  			VerticalLayout verticalLayoutWindowEditRec = new VerticalLayout();
  			HorizontalLayout horizontalLayoutWindowEditRec = new HorizontalLayout();
  			Binder<Recipe> editRecipeBinder = new Binder<>();
  			Recipe editRecipe = it.next();
  			//Сохранение дубликата, в случае отмены изменений
  			Recipe editRecipeReset = new Recipe(editRecipe.getIdRecipe(), editRecipe.getDescriptions(), editRecipe.getDateCreate(),
  					editRecipe.getTerm(), editRecipe.getIdDoctor(), editRecipe.getIdPatient(), editRecipe.getIdPriority());
  			
  			Button okButton = new Button("ОК");
  			Button cancelButton = new Button("Отмена");
  			okButton.addListener(event -> {
  				RecipeDAO editRecipeDAO = new RecipeDAO();
  				
  				//Запись в editRecipe'а новых данных
  				try {
  					editRecipeBinder.writeBean(editRecipe);
  				} catch (ValidationException e1) {
  					e1.printStackTrace();
  				}
  				
  				try {
  					editRecipeDAO.update(editRecipe);
  					editRecipeDAO.commit();
  				} catch (SQLException e1) {
  					e1.printStackTrace();
  				}
  			});
  			
  			/*
  			 * Кнопка отмены, перезапись в БД данных о рецепта,
  			 * данные которого изменяли
  			 */
  			cancelButton.addClickListener(event -> {
  				try {
  					RecipeDAO editRecipeDAORollback = new RecipeDAO();
  					editRecipeDAORollback.update(editRecipeReset);
  					editRecipeDAORollback.commit();
  				} catch (SQLException e) {
  					e.printStackTrace();
  				}
  			});
  			
  		    //Создание комбо бокса для выбора пациента из списка
  	       	PatientDAO patientDAO = new PatientDAO();
  	       	List<Patient> patientList = patientDAO.getAll();
  	       	ComboBox<Patient> comboBoxPatient = new ComboBox<Patient>("Выбирите пациента");
  	       	comboBoxPatient.setItems(patientList);
  	       	comboBoxPatient.setItemCaptionGenerator(Patient::getFIOPatient);
  	       	comboBoxPatient.setWidth("300px");
  	       	//comboBoxPatient.setEmptySelectionAllowed(false);
  	       	comboBoxPatient.setPlaceholder("Пациент не выбран...");
  	       	
  	        //Создание комбо бокса для выбора доктора из списка
  	       	DoctorDAO doctorDAO = new DoctorDAO();
  	       	List<Doctor> doctorList = doctorDAO.getAll();
  	       	ComboBox<Doctor> comboBoxDoctor = new ComboBox<Doctor>("Выбирите доктора");
  	       	comboBoxDoctor.setItems(doctorList);
  	       	comboBoxDoctor.setItemCaptionGenerator(Doctor::getFIODoctor);
  	       	comboBoxDoctor.setWidth("300px");
  	       	//comboBoxDoctor.setEmptySelectionAllowed(false);
  	       	comboBoxDoctor.setPlaceholder("Доктор не выбран...");
  	       	
  	        //Создание комбо бокса для выбора приоритета из списка
  	       	PriorityDAO priorityDAO = new PriorityDAO();
  	       	List<Priority> priorityList = priorityDAO.getAll();
  	       	ComboBox<Priority> comboBoxPriority = new ComboBox<Priority>("Выбирите приоритет");
  	       	comboBoxPriority.setItems(priorityList);
  	       	comboBoxPriority.setItemCaptionGenerator(Priority::getName);
  	       	comboBoxPriority.setWidth("300px");
  	       	comboBoxPriority.setEmptySelectionAllowed(false);
  	       	comboBoxPriority.setPlaceholder("Приоритет не выбран...");
  	       	
  	       	//TextArea для заполнения описания рецепта
  	       	TextArea textAreaDescription = new TextArea("Описание");
  	       	textAreaDescription.setSizeFull();
  	       	
  	       	//Поле для заполнения даты создания
  	       	DateField dateFieldDateCreate = new DateField("Выбирите дату создания рецепта");
  	       	dateFieldDateCreate.setLocale(new Locale("ru", "RU"));
  	       	dateFieldDateCreate.setPlaceholder("Выбирите дату");
  	       	
  	        //Поле для заполнения даты истечения
  	       	DateField dateFieldTerm = new DateField("Выбирите дату истечения рецепта");
  	       	dateFieldTerm.setLocale(new Locale("ru", "RU"));
  	       	dateFieldTerm.setPlaceholder("Выбирите дату");
  	       	
  	   		verticalLayoutWindowEditRec.addComponent(comboBoxPatient);
  	   		verticalLayoutWindowEditRec.addComponent(comboBoxDoctor);
  	   		verticalLayoutWindowEditRec.addComponent(comboBoxPriority);
  	   		verticalLayoutWindowEditRec.addComponent(dateFieldDateCreate);
  	   		verticalLayoutWindowEditRec.addComponent(dateFieldTerm);
  	   		verticalLayoutWindowEditRec.addComponent(textAreaDescription);
  	   		horizontalLayoutWindowEditRec.addComponent(okButton);
  	   		horizontalLayoutWindowEditRec.addComponent(cancelButton);
  	   		verticalLayoutWindowEditRec.addComponent(horizontalLayoutWindowEditRec);
  	   		windowEditRecipe.setContent(verticalLayoutWindowEditRec);
  	   		
  	   		editRecipeBinder.forField(textAreaDescription)
  	   			.withValidator(name -> name.matches("[а-яА-Я_._,_(_)_ _\n_Ё_ё]++") && name.length() > 4,
  	   					"Используются только символы от А - Я, следующие знаки: . , ( ) и должно состоять не менее чем из 5 букв")
  	   			.asRequired("Введите описание рецепта").bind(Recipe::getDescriptions, Recipe::setDescriptions);
  	   		editRecipeBinder.forField(comboBoxPatient)
  	   			.withConverter(new ComboBoxPatientToIdConverter())
  	   			.asRequired("Выбирите пациента")
  	   			.bind(Recipe::getIdPatient, Recipe::setIdPatient);
  	   		editRecipeBinder.forField(comboBoxDoctor)
  	   			.withConverter(new ComboBoxDoctorToIdConverter())
  				.asRequired("Выбирите доктора")
  				.bind(Recipe::getIdDoctor, Recipe::setIdDoctor);
  	   		editRecipeBinder.forField(comboBoxPriority)
  				.withConverter(new ComboBoxPriorityToIdConverter())
  				.asRequired("Выбирите приоритет")
  				.bind(Recipe::getIdPriority, Recipe::setIdPriority);
  	   		editRecipeBinder.forField(dateFieldDateCreate)
  	   			.withConverter(new localDateToDate())
  	   			.asRequired("Выбирите дату создания рецепта")
  	   			.bind(Recipe::getDateCreate, Recipe::setDateCreate);
  	   		editRecipeBinder.forField(dateFieldTerm)
  	   			.withConverter(new localDateToDate())
  	   			.asRequired("Выбирите дату истечения рецепта")
  	   			.bind(Recipe::getTerm, Recipe::setTerm);
  			editRecipeBinder.readBean(editRecipe);
  			
  			windowEditRecipe.center();
  			windowEditRecipe.setModal(true);
  			windowEditRecipe.setResizable(false);
  			windowEditRecipe.setDraggable(false);
  			windowEditRecipe.setWidth("500px");
  			
  			/*
  			 * При закрытии окна редактировании рецепта,
  			 * обновить соответвующую таблицу
  			 */
  			windowEditRecipe.addCloseListener(event -> {
  				RecipeDAO recipeDAO = new RecipeDAO();
  				verticalLayout.removeComponent(recipeGrid);
  				try {
  					verticalLayout.addComponent(showListRecipe(recipeDAO.getAll()), 1);
  				} catch (SQLException e1) {
  					e1.printStackTrace();
  				}
  			});
  			
  			addWindow(windowEditRecipe);
  		}
  	}
  	
	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}

/*
 * Конвертер для получения id пациента после вабора
 * его ФИО в соответсвующем комбобоксе
 * и наоборот, полуение пациента по его id
 */
class ComboBoxPatientToIdConverter implements Converter<Patient, Long> {

	@Override
	public Result<Long> convertToModel(Patient patient, ValueContext context) {
		if (patient == null) 
			return Result.ok((long) -1);
		return Result.ok(patient.getIdPatient());
	}

	@Override
	public Patient convertToPresentation(Long id, ValueContext context) {
		if (id > -1) {
			PatientDAO patientDAO = new PatientDAO();
			Patient patient = patientDAO.getPatient(id);
			return patient;
		}
		return null;
	}
}

/*
 * Конвертер для получения id доктора после вабора
 * его ФИО в соответсвующем комбобоксе
 * и наоборот, полуение пациента по его id
 */
class ComboBoxDoctorToIdConverter implements Converter<Doctor, Long> {

	@Override
	public Result<Long> convertToModel(Doctor doctor, ValueContext context) {
		if (doctor == null) 
			return Result.ok((long) -1);
		return Result.ok(doctor.getIdDoctor());
	}

	@Override
	public Doctor convertToPresentation(Long id, ValueContext context) {
		if (id > -1) {
			DoctorDAO doctorDAO = new DoctorDAO();
			Doctor doctor = doctorDAO.getDoctor(id);
			return doctor;
		}
		return null;
	}
}

/*
 * Конвертер для получения id приоритета после вабора
 * его ФИО в соответсвующем комбобоксе
 * и наоборот, полуение пациента по его id
 */
class ComboBoxPriorityToIdConverter implements Converter<Priority, Long> {

	@Override
	public Result<Long> convertToModel(Priority priority, ValueContext context) {
		if (priority == null) 
			return Result.ok((long) -1);
		return Result.ok(priority.getIdPriority());
	}

	@Override
	public Priority convertToPresentation(Long id, ValueContext context) {
		if (id > -1) {
			PriorityDAO priorityDAO = new PriorityDAO();
			Priority priority = priorityDAO.getPriority(id);
			return priority;
		}
		return null;
	}
}

/*
 * Конвертер для получения даты типа DATE
 * для дальнейшей вставки в БД, 
 * т.к. уомпанент DateFild возвращает дату типа LocalDate.
 * И конвертирование даты из DATE в тип LocalDate,
 * чтобы вставить в DateFild
 */
class localDateToDate implements Converter<LocalDate, java.sql.Date> {
	@Override
	public Result<java.sql.Date> convertToModel(LocalDate localDate, ValueContext context) {
		if (localDate == null)
			return null;
		return Result.ok(Date.valueOf(localDate));
	}

	@Override
	public LocalDate convertToPresentation(java.sql.Date date, ValueContext context) {
		if (date == null)
			return null;
		return date.toLocalDate();
	}
}
