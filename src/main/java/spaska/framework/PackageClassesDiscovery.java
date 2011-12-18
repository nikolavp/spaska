package spaska.framework;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that is convenient for scanning/discovering classes in packages.
 * 
 * NOTE: This code was mostly "stolen"/copied from
 * http://snippets.dzone.com/posts/show/4831
 * 
 * @author nikolavp
 * 
 */
public final class PackageClassesDiscovery {

    private static final Logger LOG = LoggerFactory
            .getLogger(PackageClassesDiscovery.class);

    private String packageName;

    /**
     * Constructs a package discovery object that can be used to discover
     * classes in the given package name.
     * 
     * @param packageName
     *            the package name in which to discover classes recursively
     */
    public PackageClassesDiscovery(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Finds subclasses of the given class in the package
     * 
     * @param baseClass
     *            the class that will be used for filtering it's subclasses when
     *            searching
     * @return all subclasses of the given baseClass param in the given package
     */
    public <T> List<Class<? extends T>> findSubclassesOf(Class<T> baseClass) {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources;
        ArrayList<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
        try {
            resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                try {
                    dirs.add(new File(resource.toURI()));
                } catch (URISyntaxException e) {
                    LOG.error(
                            "Error while converting the classpath directory to uri!",
                            e);
                }
            }
            for (File directory : dirs) {
                classes.addAll(PackageClassesDiscovery.findClasses(directory,
                        packageName, baseClass));
            }
        } catch (IOException e) {
            LOG.error("Error while getting classpath resources", e);
        }

        return classes;
    }

    private static String getClassNameFromFile(File file) {
        return file.getName().substring(0, file.getName().length() - 6);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     * 
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static <T> List<Class<? extends T>> findClasses(File directory,
            String packageName, Class<? extends T> filteredClasses) {
        List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file,
                        packageName + "." + file.getName(), filteredClasses));
            } else if (file.getName().endsWith(".class")) {
                Class<?> clazz;
                String className = packageName + '.'
                        + getClassNameFromFile(file);
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    LOG.error("Couldn't find the class with name {}",
                            className, e);
                    continue;
                }
                if(clazz == filteredClasses){
                    continue;
                }
                if (filteredClasses.isAssignableFrom(clazz)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends T> result = (Class<? extends T>) clazz;
                    classes.add(result);
                }
            }
        }
        return classes;
    }
}
