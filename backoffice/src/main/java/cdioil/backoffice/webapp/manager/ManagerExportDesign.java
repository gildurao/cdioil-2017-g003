package cdioil.backoffice.webapp.manager;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Button;

/**
 * !! DO NOT EDIT THIS FILE !!
 * <p>
 * This class is generated by Vaadin Designer and will be overwritten.
 * <p>
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class ManagerExportDesign extends CssLayout {
    protected Panel panelExport;
    protected GridLayout gridExport;
    protected Grid<cdioil.domain.Survey> gridSurveys;
    protected Button btnExportSurveyAnswers;

    public ManagerExportDesign() {
        Design.read(this);
    }
}
