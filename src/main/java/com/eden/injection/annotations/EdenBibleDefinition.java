package com.eden.injection.annotations;

import com.caseyjbrooks.clog.Clog;
import com.eden.Eden;
import com.eden.repositories.EdenRepository;
import com.eden.bible.Bible;
import com.eden.injection.EdenInjector;
import com.eden.utils.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class EdenBibleDefinition extends EdenInjector.AnnotationDefinition {
    public EdenBibleDefinition() {
        super(EdenBible.class, Bible.class, (Annotation annotation, Object targetObject, Field annotatedField) -> {
                EdenBible edenBible = (EdenBible) annotation;

                EdenRepository repository = Eden.getInstance().getRepository(edenBible.repository());

                Bible bible;
                if(TextUtils.isEmpty(edenBible.id())) {
                    bible = repository.getBible();
                }
                else {
                    bible = repository.getBible(edenBible.id());
                }

                if (annotatedField.getType().isAssignableFrom(repository.getBibleClass())) {
                    try {
                        annotatedField.set(targetObject, bible);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Clog.e("@#{$1} Error: Annotated field was of type #{$2} but #{$3} only produces Bible instances of type #{$4}",
                            new Object[] {
                                    edenBible.getClass().getSimpleName(),
                                    annotatedField.getAnnotatedType().getType().getTypeName(),
                                    repository.getClass().getSimpleName(),
                                    repository.getBibleClass().getSimpleName()
                            }
                    );
                }
            }
        );
    }
}
