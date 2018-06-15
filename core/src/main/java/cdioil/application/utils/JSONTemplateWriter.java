/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cdioil.application.utils;

import cdioil.domain.Template;
import cdioil.files.FileWriter;
import cdioil.xsl.XSLTransformer;
import java.io.File;

/**
 * Exports a template to a JSON file.
 *
 * @author <a href="1161371@isep.ipp.pt">António Sousa</a>
 * @author <a href="1161191@isep.ipp.pt">Ana Guerra</a>
 *
 * @since Version 7.0 of FeedbackMonkey
 */
public class JSONTemplateWriter implements TemplateWriter {

    /**
     * Path of the file to export.
     */
    private final String filePath;

    /**
     * Template to export (content of the file).
     */
    private final Template template;

    /**
     * Creates a new JSONTemplateWriter.
     *
     * @param filePath Path of the file to export
     * @param template Template to export
     */
    public JSONTemplateWriter(String filePath, Template template) {
        this.filePath = filePath;
        this.template = template;
    }

    /**
     * Exports the information about a template to a JSON file.
     *
     * @return true, if the file is successfully exported. Otherwise, returns false
     */
    @Override
    public boolean write() {
        XMLTemplateWriter xmlWriter = new XMLTemplateWriter(filePath, template);
        
        return FileWriter.writeFile(new File(filePath), XSLTransformer.
                create(XSLTemplateInfoDocuments.JSON_TEMPLATE_XSLT).
                transform(xmlWriter.getXMLAsString()));
    }
}
