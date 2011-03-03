package org.jboss.weld.examples.translator;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Stateful
@SessionScoped
@Named("translator")
public class TranslatorControllerBean implements TranslatorController, Serializable
{
   
   @Inject
   private TextTranslator translator;
   
   private String inputText;
   
   private String translatedText;
   
   public String getText()
   {
      return inputText;
   }
   
   public void setText(String text)
   {
      this.inputText = text;
   }
   
   public void translate()
   {
      translatedText = translator.translate(inputText);
   }
   
   public String getTranslatedText()
   {
      return translatedText;
   }
   
   @Remove
   public void remove()
   {
      
   }
   
}
