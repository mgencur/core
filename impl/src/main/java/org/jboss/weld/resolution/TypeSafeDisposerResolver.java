/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.resolution;

import java.util.Set;

import org.jboss.weld.bean.DisposalMethod;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.util.Beans;
import org.jboss.weld.util.reflection.Reflections;

/**
 * @author pmuir
 *
 */
public class TypeSafeDisposerResolver extends TypeSafeResolver<Resolvable, DisposalMethod<?, ?>>
{

   private final BeanManagerImpl manager;

   public TypeSafeDisposerResolver(BeanManagerImpl manager, Iterable<DisposalMethod<?, ?>> disposers)
   {
      super(disposers);
      this.manager = manager;
   }

   @Override
   protected boolean matches(Resolvable resolvable, DisposalMethod<?, ?> disposer)
   {
         return resolvable.getDeclaringBean().equals(disposer.getDeclaringBean()) && Reflections.isAssignableFrom(disposer.getType(), resolvable.getTypes()) && Beans.containsAllQualifiers(disposer.getQualifiers(), resolvable.getQualifiers(), manager);
   }
   
   @Override
   protected Resolvable wrap(final Resolvable resolvable)
   {
      return new ForwardingResolvable()
      {

         @Override
         protected Resolvable delegate()
         {
            return resolvable;
         }

         @Override
         public boolean equals(Object o)
         {
            if (o instanceof Resolvable)
            {
               if (super.equals(o))
               {
                  Resolvable r = (Resolvable) o;
                  return r.getDeclaringBean().equals(getDeclaringBean());
               }
            }
            return false;
         }

         @Override
         public int hashCode()
         {
            return 31 * super.hashCode() + getDeclaringBean().hashCode();
         }
      };
   }

   /**
    * @return the manager
    */
   public BeanManagerImpl getManager()
   {
      return manager;
   }

   @Override
   protected Set<DisposalMethod<?, ?>> filterResult(Set<DisposalMethod<?, ?>> matched)
   {
      return matched;
   }

   @Override
   protected Set<DisposalMethod<?, ?>> sortResult(Set<DisposalMethod<?, ?>> matched)
   {
      return matched;
   }

}
