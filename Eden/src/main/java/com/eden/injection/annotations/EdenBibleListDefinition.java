package com.eden.injection.annotations;

import com.caseyjbrooks.clog.Clog;
import com.eden.Eden;
import com.eden.repositories.EdenRepository;
import com.eden.bible.BibleList;
import com.eden.injection.EdenInjector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class EdenBibleListDefinition extends EdenInjector.AnnotationDefinition {

    public EdenBibleListDefinition() {
        super(EdenBibleList.class, BibleList.class, (Annotation annotation, Object targetObject, Field annotatedField) -> {
            EdenBibleList edenBibleList = (EdenBibleList) annotation;

            EdenRepository repository = Eden.getInstance().getRepository(edenBibleList.repository());

            BibleList bibleList = repository.getBibleList();

            if (annotatedField.getType().isAssignableFrom(repository.getBibleListClass())) {
                try {
                    annotatedField.set(targetObject, bibleList);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                Clog.e("@#{$1} Error: Annotated field was of type #{$2} but #{$3} only produces BibleList instances of type #{$4}",
                        new Object[]{
                                edenBibleList.getClass().getSimpleName(),
                                annotatedField.getType().getSimpleName(),
                                repository.getClass().getSimpleName(),
                                repository.getBibleListClass().getSimpleName()
                        }
                );
            }
        });
    }
}
