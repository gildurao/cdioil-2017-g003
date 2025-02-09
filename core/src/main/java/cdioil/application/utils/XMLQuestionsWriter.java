package cdioil.application.utils;

import cdioil.domain.BinaryQuestion;
import cdioil.domain.MultipleChoiceQuestion;
import cdioil.domain.QuantitativeQuestion;
import cdioil.domain.QuantitativeQuestionOption;
import cdioil.domain.Question;
import cdioil.domain.QuestionOption;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Exports a Questions to a .xml file.
 *
 * @author João
 */
public class XMLQuestionsWriter {

    /**
     * Constant that represents the label used for the question list on the XML file
     */
    private static final String QUESTION_LIST_LABEL = "lista_questoes";
    /**
     * Constant that represents the label used for the question identifier on the XML file
     */
    private static final String QUESTION_LABEL = "question";
    /**
     * Constant that represents the label used for the binary question identifier on the XML file
     */
    private static final String BINARY_QUESTION_LABEL = "BinaryQuestion";
    /**
     * Constant that represents the label used for the quantitative question identifier on the XML file
     */
    private static final String QUANTITATIVE_QUESTION_LABEL = "QuantitativeQuestion";
    /**
     * Constant that represents the label used for the scale minimum value identifier on the XML file
     */
    private static final String SCALE_MIN_VALUE_LABEL = "scaleMinValue";
    /**
     * Constant that represents the label used for the scale maximum value identifier on the XML file
     */
    private static final String SCALE_MAX_VALUE_LABEL = "scaleMaxValue";
    /**
     * Constant that represents the label used for the multiple choice question identifier on the XML file
     */
    private static final String MCQ_LABEL = "MultipleChoiceQuestion";
    /**
     * Constant that represents the label used for the text identifier on the XML file
     */
    private static final String TEXT_LABEL = "Text";
    /**
     * Constant that represents the label used for the option identifier on the XML file
     */
    private static final String OPTION_LABEL = "Option";
    /**
     * Constant that represents the label used for the num attribute identifier on the XML file
     */
    private static final String NUM_ATTR = "num";
    /**
     * Constant that represents the label used for the text attribute identifier on the XML file
     */
    private static final String TEXT_ATTR = "text";
    /**
     * Constant that represents the label used for the question ID attribute identifier on the XML file
     */
    private static final String QUESTION_ID_LABEL = "questionID";
    /**
     * Constant that represents the label used for the category path identifier on the XML file
     */
    private static final String CATID_LABEL = "CAT_ID";
    
    /**
     * Puts the .csv file content into an .xml file
     *
     * @param questionMap map of category paths and questions
     * @return document
     */
    public Document categoryQuestionsToXMLDocument(Map<String, List<Question>> questionMap) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            writeQuestions(doc, questionMap);
            return doc;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLQuestionsWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Writes the questions into a XML file.
     *
     * @param doc Representation of the XML document
     */
    private void writeQuestions(Document doc, Map<String, List<Question>> readQuestions) {
        Element rootElement = doc.createElement(QUESTION_LIST_LABEL);
        doc.appendChild(rootElement);
        for (String catPath : readQuestions.keySet()) {
            for (Question question : readQuestions.get(catPath)) {
                Element questionElement = doc.createElement(QUESTION_LABEL);
                rootElement.appendChild(questionElement);

                Element qElement = null;
                if (question instanceof BinaryQuestion) {
                    qElement = doc.createElement(BINARY_QUESTION_LABEL);
                    Element txtElement = doc.createElement(TEXT_LABEL);
                    txtElement.setTextContent(question.getQuestionText());
                    qElement.appendChild(txtElement);
                } else if (question instanceof QuantitativeQuestion) {
                    qElement = doc.createElement(QUANTITATIVE_QUESTION_LABEL);
                    Element txtElement = doc.createElement(TEXT_LABEL);
                    txtElement.setTextContent(question.getQuestionText());
                    qElement.appendChild(txtElement);

                    int min = ((QuantitativeQuestionOption) question.getOptionList().get(0)).getContent().intValue();
                    int max = ((QuantitativeQuestionOption) question.getOptionList().get(question.getOptionList().size() - 1)).getContent().intValue();
                    Element minElement = doc.createElement(SCALE_MIN_VALUE_LABEL);
                    minElement.setTextContent(Integer.toString(min));
                    qElement.appendChild(minElement);
                    Element maxElement = doc.createElement(SCALE_MAX_VALUE_LABEL);
                    maxElement.setTextContent(Integer.toString(max));
                    qElement.appendChild(maxElement);
                } else if (question instanceof MultipleChoiceQuestion) {
                    qElement = doc.createElement(MCQ_LABEL);

                    Element txtElement = doc.createElement(TEXT_LABEL);
                    txtElement.setTextContent(question.getQuestionText());
                    qElement.appendChild(txtElement);

                    int i = 1;
                    for (QuestionOption option : question.getOptionList()) {
                        Element optionElement = doc.createElement(OPTION_LABEL);

                        Attr attrNum = doc.createAttribute(NUM_ATTR);
                        attrNum.setValue(Integer.toString(i));
                        optionElement.setAttributeNode(attrNum);

                        Attr attrText = doc.createAttribute(TEXT_ATTR);
                        attrText.setValue(option.toString());
                        optionElement.setAttributeNode(attrText);

                        qElement.appendChild(optionElement);
                        i++;
                    }
                }

                Attr attrID = doc.createAttribute(QUESTION_ID_LABEL);
                attrID.setValue(question.getQuestionID());
                //NO SONAR COMMENT BELOW IS DUE TO AN EXCEPTION WOULD BE ALREADY THROWN IF NULL
                qElement.setAttributeNode(attrID);//NOSONAR 

                questionElement.appendChild(qElement);

                Element catElement = doc.createElement(CATID_LABEL);
                catElement.setTextContent(catPath);
                questionElement.appendChild(catElement);
            }
        }
    }
}
