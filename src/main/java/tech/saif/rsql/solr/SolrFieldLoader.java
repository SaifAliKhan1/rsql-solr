package tech.saif.rsql.solr;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.solr.core.mapping.Indexed;

/**
 *
 * @author Saif Ali Khan
 *
 */
public class SolrFieldLoader {

    private SolrFieldLoader() {

    }

    private static final ConcurrentMap<Class<?>, ConcurrentMap<String, String>> fieldsMap = new ConcurrentHashMap<>();

    public static String getSolrFieldName(Class<?> clazz, String name) {
        if (!fieldsMap.containsKey(clazz)) {
            populateSolrDaoFieldsForClass(clazz);
        }
        return Optional.ofNullable(fieldsMap.get(clazz)).filter(m -> m.containsKey(name)).map(m -> m.get(name))
                .orElse(name);
    }

    public static Map<String, String> getSolrFieldMap(Class<?> clazz) {
        if (!fieldsMap.containsKey(clazz)) {
            populateSolrDaoFieldsForClass(clazz);
        }
        return Collections.unmodifiableMap(fieldsMap.get(clazz));
    }

    private static void populateSolrDaoFieldsForClass(Class<?> clazz) {
        Class<?> searchClazz = clazz;
        while (isvalidClass(searchClazz)) {
            for (Field field : searchClazz.getDeclaredFields()) {
                String fieldName = field.getName();
                String daoFieldName = fieldName;
                Indexed indexedAnnotation = field.getAnnotation(Indexed.class);
                org.apache.solr.client.solrj.beans.Field fieldAnnotation =
                        field.getAnnotation(org.apache.solr.client.solrj.beans.Field.class);
                if (indexedAnnotation != null) {
                    daoFieldName = Optional.ofNullable(indexedAnnotation.value()).filter(StringUtils::isNotBlank)
                            .orElse(daoFieldName);
                }
                else if (fieldAnnotation != null) {
                    daoFieldName = Optional.ofNullable(fieldAnnotation.value()).filter(StringUtils::isNotBlank)
                            .orElse(daoFieldName);
                }
                fieldsMap.putIfAbsent(clazz, new ConcurrentHashMap<>());
                fieldsMap.get(clazz).putIfAbsent(fieldName, daoFieldName);
            }
            searchClazz = searchClazz.getSuperclass();
        }
    }

    private static boolean isvalidClass(Class<?> searchClazz) {
        return searchClazz != null && !searchClazz.isInterface()
                && !searchClazz.isPrimitive()
                && !searchClazz.isSynthetic()
                && !searchClazz.isAnnotation()
                && !Collection.class.isAssignableFrom(searchClazz)
                && !Map.class.isAssignableFrom(searchClazz)
                && !searchClazz.equals(Object.class);
    }
}
