package cdioil.xsl;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * XSLTransformer class that transforms XML documents with a XSL document
 * @author <a href="1160907@isep.ipp.pt">João Freitas</a>
 * @since Version 7.0 of FeedbackMonkey
 */
public final class XSLTransformer {
    /**
     * Transformer with the current XSL transformer
     */
    private final Transformer xslTransformer;
    /**
     * Creates a new XSLTransformer with a certain XSL document
     * @param xslDocument File with the XSL document that will serve as transformer
     * @return XSLTransformer with the new transformer
     * @throws IllegalStateException if the XSL document is invalid
     */
    public static XSLTransformer create(File xslDocument){
        try{
            return new XSLTransformer(new StreamSource(xslDocument));
        }catch(TransformerConfigurationException transformerConfigurationException){
            throw new IllegalStateException(transformerConfigurationException.getException());
        }
    }
    /**
     * Creates a new XSLTransformer with a certain XSL document
     * @param xslDocument String with the XSL document that will serve as transformer
     * @return XSLTransformer with the new transformer
     * @throws IllegalStateException if the XSL document is invalid
     */
    public static XSLTransformer create(String xslDocument){
        try{
            return new XSLTransformer(new StreamSource(new StringReader(xslDocument)));
        }catch(TransformerConfigurationException transformerConfigurationException){
            throw new IllegalStateException(transformerConfigurationException.getException());
        }
    }
    /**
     * Applies a XSL Transformation on a given XML document
     * @param xmlDocument File with the XML document being applied the transformation
     * @return String with the transformed document content
     * @throws IllegalStateException if the XML document is invalid
     */
    public String transform(File xmlDocument){
        return transform(new StreamSource(xmlDocument));
    }
    /**
     * Applies a XSL Transformation on a given XML document
     * @param xmlDocument File with the XML document being applied the transformation
     * @return String with the transformed document content
     * @throws IllegalStateException if the XML document is invalid
     */
    public String transform(String xmlDocument){
        return transform(new StreamSource(new StringReader(xmlDocument)));
    }
    /**
     * Applies a XSL Transformation on a certain source representing a XML document
     * @param xslSource Source with the source containing the XML document being applied on
     * @return String with the transformed document content
     * @throws IllegalStateException if the XML document is invalid
     */
    private String transform(Source xslSource){
        try{
            StringWriter transformedDocument=new StringWriter();
            xslTransformer.transform(xslSource,new StreamResult(transformedDocument));
            return transformedDocument.getBuffer().toString();
        }catch(TransformerException transformerException){
            throw new IllegalStateException(transformerException.getException());
        }
    }
    /**
     * Builds a new XSLTransformer with a certain XSL document
     * @param xslDocumentSource Source with the source of the XSL document
     */
    private XSLTransformer(Source xslDocumentSource) throws TransformerConfigurationException{
        xslTransformer=TransformerFactory.newInstance().newTransformer(xslDocumentSource);
    }
}
