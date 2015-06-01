package me.pbox.site.util;

import com.codeforces.commons.pair.SimplePair;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import org.nocturne.annotation.Action;
import org.nocturne.annotation.Invalid;
import org.nocturne.annotation.Validate;
import org.nocturne.main.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Edvard Davtyan (homo_sapiens@xakep.ru)
 */
public class ReflectionUtil {
    private static final ConcurrentMap<ClassAndAnnotationClass, AnnotatedSuperclassAndAnnotation>
            annotatedSuperclassAndAnnotationByClassAndAnnotationClass = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, Class<?>> realClassByGeneratedClass = new ConcurrentHashMap<>();

    public static Matcher<? super Class<?>> newClassOrSuperclassAnnotatedWithMatcher(
            final Class<? extends Annotation> annotationClass) {
        return new AbstractMatcher<Class<?>>() {
            @Override
            public boolean matches(Class<?> clazz) {
                return isClassOrSuperclassAnnotatedWith(clazz, annotationClass);
            }
        };
    }

    public static Matcher<? super Method> newComponentActionMethodMatcher() {
        return new AbstractMatcher<Method>() {
            @Override
            public boolean matches(Method method) {
                return "action".equals(method.getName())
                        || "validate".equals(method.getName())
                        || "invalid".equals(method.getName())
                        || method.isAnnotationPresent(Action.class)
                        || method.isAnnotationPresent(Validate.class)
                        || method.isAnnotationPresent(Invalid.class);
            }
        };
    }

    /**
     * @param clazz           Clazz.
     * @param annotationClass Annotation clazz.
     * @return {@code true} iff clazz or any superclass has specified annotation.
     */
    public static boolean isClassOrSuperclassAnnotatedWith(
            @Nonnull Class<?> clazz, @Nonnull Class<? extends Annotation> annotationClass) {
        return getAnnotatedSuperclassAndAnnotation(clazz, annotationClass).getAnnotation() != null;
    }

    @Nonnull
    public static AnnotatedSuperclassAndAnnotation getAnnotatedSuperclassAndAnnotation(
            @Nonnull Class<?> clazz, @Nonnull Class<? extends Annotation> annotationClass) {
        ClassAndAnnotationClass classAndAnnotationClass = new ClassAndAnnotationClass(clazz, annotationClass);

        AnnotatedSuperclassAndAnnotation annotatedSuperclassAndAnnotation
                = annotatedSuperclassAndAnnotationByClassAndAnnotationClass.get(classAndAnnotationClass);

        if (annotatedSuperclassAndAnnotation == null) {
            Annotation annotation = null;
            while (clazz != null && (annotation = clazz.getAnnotation(annotationClass)) == null) {
                clazz = clazz.getSuperclass();
            }
            annotatedSuperclassAndAnnotationByClassAndAnnotationClass.putIfAbsent(
                    classAndAnnotationClass, new AnnotatedSuperclassAndAnnotation(clazz, annotation)
            );
            annotatedSuperclassAndAnnotation
                    = annotatedSuperclassAndAnnotationByClassAndAnnotationClass.get(classAndAnnotationClass);
        }

        return annotatedSuperclassAndAnnotation;
    }

    /**
     * @param clazz Component class.
     * @return Original class can be wrapped by Google Guice because of IoC.
     *         The method returns original class by possible wrapped.
     */
    @SuppressWarnings("ConstantConditions")
    public static Class<?> getRealComponentClass(Class<? extends Component> clazz) {
        Class<?> result = realClassByGeneratedClass.get(clazz);
        if (result != null) {
            return result;
        }

        result = clazz;
        while (isGeneratedClass(result)) {
            result = result.getSuperclass();
        }

        realClassByGeneratedClass.putIfAbsent(clazz, result);
        return realClassByGeneratedClass.get(clazz);
    }

    private static boolean isGeneratedClass(Class<?> clazz) {
        return clazz.getName().contains("$$") || clazz.getName().contains("EnhancerByGuice");
    }

    public static String getActionNameByMethod(Method method) {
        if (method.isAnnotationPresent(Action.class)) {
            return method.getAnnotation(Action.class).value();
        } else if (method.isAnnotationPresent(Validate.class)) {
            return method.getAnnotation(Validate.class).value();
        } else if (method.isAnnotationPresent(Invalid.class)) {
            return method.getAnnotation(Invalid.class).value();
        } else {
            return "";
        }
    }

    private static final class ClassAndAnnotationClass extends SimplePair<Class<?>, Class<? extends Annotation>> {
        private ClassAndAnnotationClass(
                @Nullable Class<?> clazz, @Nullable Class<? extends Annotation> annotationClass) {
            super(clazz, annotationClass);
        }
    }

    public static final class AnnotatedSuperclassAndAnnotation extends SimplePair<Class<?>, Annotation> {
        private AnnotatedSuperclassAndAnnotation(
                @Nullable Class<?> annotatedSuperclass, @Nullable Annotation annotation) {
            super(annotatedSuperclass, annotation);
        }

        @Nullable
        public Class<?> getAnnotatedSuperclass() {
            return getFirst();
        }

        public void setAnnotatedSuperclass(@Nullable Class<?> annotatedSuperclass) {
            setFirst(annotatedSuperclass);
        }

        @Nullable
        public Annotation getAnnotation() {
            return getSecond();
        }

        public void setAnnotation(@Nullable Annotation annotation) {
            setSecond(annotation);
        }
    }
}
