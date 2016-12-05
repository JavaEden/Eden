package com.eden.annotations;

import com.caseyjbrooks.clog.Clog;
import com.eden.Eden;
import com.eden.EdenRepository;
import com.eden.bible.Bible;
import com.eden.bible.BibleList;
import com.eden.utils.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class AnnotationProcessor {

// Singleton
//----------------------------------------------------------------------------------------------------------------------
    private static AnnotationProcessor instance;

    public static AnnotationProcessor getInstance() {
        if(instance == null) {
            instance = new AnnotationProcessor();
        }

        return instance;
    }

// Instance Definition
//----------------------------------------------------------------------------------------------------------------------
    List<AnnotationDefinition> annotationDefinitionList;
    private AnnotationProcessor() {
        this.annotationDefinitionList = new ArrayList<>();
        this.annotationDefinitionList.add(edenBibleDefinition);
        this.annotationDefinitionList.add(edenBibleListDefinition);
    }

    public void processAnnotations(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            for(AnnotationDefinition definition : annotationDefinitionList) {
                if (field.isAnnotationPresent(definition.annotationClass)) {
                    if (definition.fieldTypeClass.isAssignableFrom(field.getType())) {
                        definition.annotationHandler.handle(field.getAnnotation(definition.annotationClass), object, field);
                    }
                    else {
                        Clog.e("@#{$1} annotation must be used on a field of type #{$2} or one of its subclasses.",
                            new Object[]{
                                   definition.annotationClass.getSimpleName(),
                                    definition.fieldTypeClass.getSimpleName()
                            }
                        );
                    }
                }
            }
        }
    }

// Interfaces Implementations for default annotations
//----------------------------------------------------------------------------------------------------------------------
    private AnnotationDefinition edenBibleDefinition = new AnnotationDefinition(
        EdenBible.class,
        Bible.class,
        (Annotation annotation, Object targetObject, Field annotatedField) -> {
            EdenBible edenBible = (EdenBible) annotation;

            EdenRepository repository = Eden.getInstance().getRepository(edenBible.repository());

            Bible bible;
            if(TextUtils.isEmpty(edenBible.id())) {
                bible = repository.getSelectedBible();
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

    private AnnotationDefinition edenBibleListDefinition = new AnnotationDefinition(
        EdenBibleList.class,
        BibleList.class,

        (Annotation annotation, Object targetObject, Field annotatedField) -> {
            EdenBibleList edenBibleList = (EdenBibleList) annotation;

            EdenRepository repository = Eden.getInstance().getRepository(edenBibleList.repository());

            BibleList bibleList = repository.getBibleList();

            if (annotatedField.getType().isAssignableFrom(repository.getBibleListClass())) {
                try {
                    annotatedField.set(targetObject, bibleList);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                Clog.e("@#{$1} Error: Annotated field was of type #{$2} but #{$3} only produces BibleList instances of type #{$4}",
                    new Object[] {
                        edenBibleList.getClass().getSimpleName(),
                        annotatedField.getType().getSimpleName(),
                        repository.getClass().getSimpleName(),
                        repository.getBibleListClass().getSimpleName()
                    }
                );
            }
        }
    );


// Interfaces
//----------------------------------------------------------------------------------------------------------------------

    public interface AnnotationHandler {
        void handle(Annotation annotation, Object targetObject, Field annotatedField);
    }

    public class AnnotationDefinition {
        Class<? extends Annotation> annotationClass;
        Class<?> fieldTypeClass;
        AnnotationHandler annotationHandler;

        AnnotationDefinition(Class<? extends Annotation> annotationClass, Class<?> fieldTypeClass, AnnotationHandler annotationHandler) {
            this.annotationClass = annotationClass;
            this.fieldTypeClass = fieldTypeClass;
            this.annotationHandler = annotationHandler;
        }
    }
}
