package spaska.framework;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    private static final int CLASS_SUFFIX_LENGTH = 6;

    private static final Logger LOG = LoggerFactory
            .getLogger(PackageClassesDiscovery.class);

    private String packageName;

    private String packagePath;

    /**
     * Constructs a package discovery object that can be used to discover
     * classes in the given package name.
     * 
     * @param packageName
     *            the package name in which to discover classes recursively
     */
    public PackageClassesDiscovery(String packageName) {
        this.packageName = packageName;
        this.packagePath = packageName.replace('.', '/');
    }

    /**
     * Finds subclasses of the given class in the package.
     * 
     * @param baseClass
     *            the class that will be used for filtering it's subclasses when
     *            searching
     * @return all subclasses of the given baseClass param in the given package
     * @param <T>
     *            the type of classes that should be found
     */
    public <T> List<Class<? extends T>> findSubclassesOf(Class<T> baseClass) {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        assert classLoader != null;

        Enumeration<URL> resources;
        List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
        try {
            resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                try {
                    String dirPath = resource.toURI().toASCIIString()
                            .replaceFirst("file:", "");
                    if (dirPath.startsWith("jar:")) {
                        findClassesInJar(dirPath, classes, baseClass);
                    } else {
                        classes.addAll(PackageClassesDiscovery.findClasses(
                                new File(dirPath), packageName, baseClass));
                    }
                } catch (URISyntaxException e) {
                    LOG.error(
                            "Error while converting the classpath directory to uri!",
                            e);
                }
            }
        } catch (IOException e) {
            LOG.error("Error while getting classpath resources", e);
        }

        return classes;
    }

    /**
     * Finds classes in a jar file.
     * 
     * @param dirPath
     *            the dir path
     * @param classes
     *            the output classes that is expected
     * @param baseClass
     *            the base class that we want to get the subclasses of
     * @throws IOException
     *             if any exception occurs while reading the file in the zip
     * @throws ZipException
     *             if any exception occurs while reading the zip file
     */
    private <T> void findClassesInJar(String dirPath,
            List<Class<? extends T>> classes, Class<? extends T> baseClass)
            throws IOException {
        dirPath = dirPath.replaceFirst("jar:", "").replaceFirst("file:", "")
                .replaceAll("!.*$", "");
        ZipFile file = new ZipFile(new File(dirPath));
        Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (entryName.startsWith(packagePath)
                    && entryName.endsWith(".class")) {
                String classNameFromFile = getClassNameFromFile(entryName);
                addClass(classNameFromFile.replaceAll("/", "."), baseClass,
                        classes);
            }
        }
    }

    private static String getClassNameFromFile(String fileName) {
        return fileName.substring(0, fileName.length() - CLASS_SUFFIX_LENGTH);
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
                String className = packageName + '.'
                        + getClassNameFromFile(file.getName());
                addClass(className, filteredClasses, classes);
            }
        }
        return classes;
    }

    /**
     * Add a single class to the list of found classes.
     * 
     * @param className
     *            the class name to be considered for adding
     * @param filteredClasses
     *            the base class we are searching the subclasses of.
     * @param classes
     *            the list that needs to be populated with the class if needed
     */
    private static <T> void addClass(String className,
            Class<?> filteredClasses, List<Class<? extends T>> classes) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            LOG.error("Couldn't find the class with name {}", className, e);
            return;
        }
        if (clazz == filteredClasses) {
            return;
        }
        if (filteredClasses.isAssignableFrom(clazz)) {
            @SuppressWarnings("unchecked")
            Class<? extends T> result = (Class<? extends T>) clazz;
            classes.add(result);
        }
    }
}
