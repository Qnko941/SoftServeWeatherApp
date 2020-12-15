package com.bwap.weatherapp.WeatherApp.views;

import com.bwap.weatherapp.WeatherApp.controller.WeatherService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import elemental.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.IOException;
import java.util.ArrayList;

@SpringUI(path = "")
public class MainView extends UI {

    @Autowired
    private WeatherService weatherService;

    private VerticalLayout mainLayout;
    private NativeSelect<String> unitSelect;
    private TextField cityTextField;
    private Button searchButton;
    private HorizontalLayout dashboard;
    private Label location;
    private Label currentTemp;
    private HorizontalLayout mainDescriptionLayout;
    private Label weatherDescription;
    private Label maxWeather;
    private Label minWeather;
    private Label Pressure;
    private Label Humidity;
    private Label Wind;
    private Label FeelsLike;
    private Image icon;


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        mainLayout();
        setHeader();
        setLogo();
        setForm();
        dashboardTitle();
        dashboardDetails();


        searchButton.addClickListener(clickEvent -> {
            if (!cityTextField.getValue().equals("")) {
                try {
                    updateUI();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Please enter city name");
            }
        });
    }

    private void mainLayout() {
        icon = new Image();
        mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        setContent(mainLayout);
    }

    private void setHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Label title = new Label("Weather APP by Yanislav Tomov");

        header.addComponent(title);

        mainLayout.addComponent(header);
    }

    private void setLogo() {
        HorizontalLayout logo = new HorizontalLayout();
        logo.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Image img = new Image(null, new ClassResource("/static/download.png"));
        logo.setWidth("240px");
        logo.setHeight("240px");

        logo.addComponent(img);
        mainLayout.addComponent(logo);

    }

    private void setForm() {
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formLayout.setSpacing(true);
        formLayout.setMargin(true);
        unitSelect = new NativeSelect<>();
        ArrayList<String> items = new ArrayList<>();
        items.add("C");
        items.add("F");

        unitSelect.setItems(items);
        unitSelect.setValue(items.get(0));
        formLayout.addComponent(unitSelect);

        //cityTextField

        cityTextField = new TextField();
        cityTextField.setWidth("80%");
        formLayout.addComponent(cityTextField);

        //Search button
        searchButton = new Button();
        searchButton.setIcon(VaadinIcons.SEARCH);
        formLayout.addComponent(searchButton);

        mainLayout.addComponents(formLayout);

    }

    private void dashboardTitle() {
        dashboard = new HorizontalLayout();
        dashboard.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //city location
        location = new Label("Currently in Sofia");
        location.addStyleName(ValoTheme.LABEL_H2);
        location.addStyleName(ValoTheme.LABEL_LIGHT);

        //current TEMP

        currentTemp = new Label("10c");
        currentTemp.setStyleName(ValoTheme.LABEL_BOLD);
        currentTemp.setStyleName(ValoTheme.LABEL_H1);

        dashboard.addComponents(location, icon, currentTemp);


    }

    private void dashboardDetails() {
        mainDescriptionLayout = new HorizontalLayout();
        mainDescriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //description layout
        VerticalLayout descriptionLayout = new VerticalLayout();
        descriptionLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        //Weather description

        weatherDescription = new Label("Description: Clear Skies");
        weatherDescription.setStyleName(ValoTheme.LABEL_SUCCESS);
        descriptionLayout.addComponent(weatherDescription);

        //Min Weather
        minWeather = new Label("Min:53");
        descriptionLayout.addComponent(minWeather);
        //Max Weather
        maxWeather = new Label("Max:53");
        descriptionLayout.addComponent(maxWeather);
        mainDescriptionLayout.addComponent(descriptionLayout);

        VerticalLayout pressureLayout = new VerticalLayout();
        pressureLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Pressure = new Label("pressure: 231Pa");
        pressureLayout.addComponent(Pressure);

        Humidity = new Label("Humidity: 23");
        pressureLayout.addComponent(Humidity);

        Wind = new Label("Wind: 231");
        pressureLayout.addComponent(Wind);

        FeelsLike = new Label("FeelsLike: 231Pa");
        pressureLayout.addComponent(FeelsLike);

        mainDescriptionLayout.addComponents(descriptionLayout, pressureLayout);

    }

    private void updateUI() throws JSONException, IOException {
        String city = cityTextField.getValue();
        String defaultUnit;
        weatherService.setCityName(city);

        if (unitSelect.getValue().equals("F")) {
            weatherService.setUnit("imperial");
            unitSelect.setValue("F");
            defaultUnit = "\u00b0" + "F";
        } else {
            weatherService.setUnit("metric");
            defaultUnit = "\u00b0" + "C";
            unitSelect.setValue("C");
        }

        location.setValue("Currently in " + city);
        JSONObject mainObject = weatherService.returnMain();
        int temp = mainObject.getInt("temp");
        currentTemp.setValue(temp + defaultUnit);

        String iconCode = null;
        String weatherDescriptionNew = null;
        JSONArray jsonArray = weatherService.returnWeatherArray();
        for (int i = 0; i <jsonArray.length() ; i++) {
            JSONObject weatherObj = jsonArray.getJSONObject(i);
            iconCode = weatherObj.getString("icon");
            weatherDescriptionNew = weatherObj.getString("description");
            
        }
        icon.setSource(new ExternalResource("http://openweathermap.org/img/wn/"+ iconCode +"@2x.png"));

        weatherDescription.setValue("Description:" + weatherDescriptionNew);
        minWeather.setValue("Min Temp: " + weatherService.returnMain().getInt("temp_min")+ unitSelect.getValue());
        maxWeather.setValue("Max Temp: " + weatherService.returnMain().getInt("temp_max")+ unitSelect.getValue());
        Pressure.setValue("Pressure: "+ weatherService.returnMain().get("pressure"));
        Humidity.setValue("Humidity: "+ weatherService.returnMain().get("humidity"));
        Wind.setValue("Wind: "+weatherService.returnWind().getInt("speed"));
        FeelsLike.setValue("Feels Like" + weatherService.returnMain().getDouble("feels_like"));


        mainLayout.addComponents(dashboard, mainDescriptionLayout);
    }



}
