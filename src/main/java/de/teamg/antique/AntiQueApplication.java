package de.teamg.antique;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "antique", variant = Lumo.LIGHT)
public class AntiQueApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(AntiQueApplication.class, args);
    }

}
