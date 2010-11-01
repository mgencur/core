/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc. and/or its affiliates, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.weld.bean.proxy;

import static org.jboss.weld.logging.Category.BEAN;
import static org.jboss.weld.logging.LoggerFactory.loggerFactory;
import static org.jboss.weld.logging.messages.BeanMessage.BEAN_INSTANCE_NOT_SET_ON_PROXY;

import java.io.Serializable;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import org.jboss.interceptor.util.proxy.TargetInstanceProxy;
import org.jboss.weld.exceptions.WeldException;
import org.slf4j.cal10n.LocLogger;

/**
 * A general purpose MethodHandler for all proxies which routes calls to the
 * {@link BeanInstance} associated with this proxy or handler.
 * 
 * @author David Allen
 *
 */
public class ProxyMethodHandler implements MethodHandler, Serializable
{
   // The log provider
   protected static final LocLogger log = loggerFactory().getLogger(BEAN);

   // The bean instance to forward calls to
   private BeanInstance beanInstance;

   public ProxyMethodHandler(BeanInstance beanInstance)
   {
      this.beanInstance = beanInstance;
   }

   /* (non-Javadoc)
    * @see javassist.util.proxy.MethodHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.Object[])
    */
   public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
   {
      if (thisMethod == null)
      {
         log.trace("MethodHandler processing returning bean instance for " + self.getClass());
         if (beanInstance == null)
         {
            throw new WeldException(BEAN_INSTANCE_NOT_SET_ON_PROXY);
         }
         return beanInstance.getInstance();
      }
      log.trace("MethodHandler processing call to " + thisMethod + " for " + self.getClass());
      if (thisMethod.getDeclaringClass().equals(TargetInstanceProxy.class))
      {
         if (beanInstance == null)
         {
            throw new WeldException(BEAN_INSTANCE_NOT_SET_ON_PROXY);
         }
         if (thisMethod.getName().equals("getTargetInstance"))
         {
            return beanInstance.getInstance();
         }
         else if (thisMethod.getName().equals("getTargetClass"))
         {
            return beanInstance.getInstanceType();
         }
         else
         {
            return null;
         }
      }
      else if (thisMethod.getName().equals("writeReplace"))
      {
         return new org.jboss.weld.bean.proxy.util.SerializableProxy(self);
      }
      else if (thisMethod.getName().equals("_initMH"))
      {
         log.trace("Setting new MethodHandler with bean instance for " + args[0] + " on " + self.getClass());
         return new ProxyMethodHandler(new TargetBeanInstance(args[0]));
      }
      else
      {
         if (beanInstance == null)
         {
            throw new WeldException(BEAN_INSTANCE_NOT_SET_ON_PROXY);
         }
         return beanInstance.invoke(thisMethod, args);
      }
   }

}
