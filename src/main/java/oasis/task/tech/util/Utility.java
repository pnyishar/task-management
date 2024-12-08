package oasis.task.tech.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.management.OperatingSystemMXBean;
import oasis.task.tech.dao.Accessor;
import oasis.task.tech.dao.Pager;
import oasis.task.tech.dao.Param;
import oasis.task.tech.domains.base.Identifiable;
import oasis.task.tech.domains.security.Permission;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Utility {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public static Map<String, String> errors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return errors;
    }


    public static Map<String, String> getRequestParams(HttpServletRequest r) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> e = r.getParameterNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement();
            params.put(name, r.getParameter(name));
        }
        return params;
    }


    public static Param getParam(HttpServletRequest req){
        Param p = new Param(0, 50);
        String page = req.getParameter("page");
        if (StringUtils.isNumeric(page)) p.setPage(Integer.valueOf(page));
        String size = req.getParameter("size");
        if (StringUtils.isNumeric(size)) p.setSize(Integer.valueOf(size));
        return p;
    }

    public static Param getParam(Pageable pageable){
        Param p = new Param(0, 50);
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        p.setPage(page);
        p.setSize(size);
        return p;
    }

    public static <T> List<T> getPagedEntities(Pager pager, List<T> entities){
        List<T> pagedEntities = new ArrayList<>();
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }

        int pageSize = pager.getSize();
        int currentPage = pager.getPage();
        if(currentPage == 0){
            currentPage = 1;
        }
        int total = entities.size();

        int startAt = 0;
        int end = pageSize;
        if (currentPage > 1) {
            startAt = (pageSize * currentPage) - pageSize;
            if(startAt > total){
                return Collections.emptyList();
            }
            end = startAt + pageSize;
        }

        if(end > total){
            end = total;
        }

        for(int i=startAt; i<end; i++){
            T entity = entities.get(i);
            if(entity != null) {
                pagedEntities.add(entity);
            }
        }

        return pagedEntities;
    }

    public static Pager getPager(int total, HttpServletRequest request){
        Param p = Utility.getParam(request);
        if(p.getPage() == 0){
            p.setPage(1);
        }
        return new Pager(total, p.getPage(), p.getSize(), p.getUrl());
    }

    public static Pager getPager(int total, Pageable pageable){
        Param p = Utility.getParam(pageable);
        if(p.getPage() == 0){
            p.setPage(1);
        }
        return new Pager(total, p.getPage(), p.getSize(), p.getUrl());
    }

    public static String generateClassName(String classLevelName, String classArm) throws IllegalArgumentException {
        if (StringUtils.isBlank(classLevelName) || StringUtils.isBlank(classArm)) {
            throw new IllegalArgumentException("class Type must not be null, classLevel cannot be zero and classArm cannot be empty");
        }

        StringBuilder sb = new StringBuilder(classLevelName.trim());
        sb.append(" ").append(classArm.trim());

        return sb.toString();
    }


    public static String getGenderPronoun1(String gender) {

        if (StringUtils.isBlank(gender)) return "his";//default to male
        gender = gender.replaceAll("[\\s]+", "");
        return "F".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender) ? "her" : "his";

    }


    public static String getGenderPronoun2(String gender) {

        if (StringUtils.isBlank(gender)) return "him";//default to male
        gender = gender.replaceAll("[\\s]+", "");
        return "F".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender) ? "her" : "him";
    }


    public static String getGenderPronoun3(String gender) {
        if (StringUtils.isBlank(gender)) {
            return "he";//default to male
        }

        gender = gender.replaceAll("[\\s]+", "");

        if ("M".equalsIgnoreCase(gender) || "Male".equalsIgnoreCase(gender)) {
            return "he";
        }

        if ("F".equalsIgnoreCase(gender) || "Female".equalsIgnoreCase(gender)) {
            return "she";
        }

        return "he";

    }

    public static <T> T fromJson(String sourceJson, Class<T> targetClass) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        T object = objectMapper.readValue(sourceJson, targetClass);
        return object;
    }

    public static ObjectMapper getObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    public static String createJsonStringFromObject(Object object) {
        ObjectWriter ow = getObjectMapper().writer().withDefaultPrettyPrinter();
        String json = "";
        try {
            json = ow.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return json;
    }

    public static <T> T map(String json, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.warn("Error occur during mapping {}", e.getMessage());
        }
        return newInstance(clazz);
    }

    public static String toJson(Object object) {
        return createJsonStringFromObject(object);
    }

    private static Path buildPath(final Path root, final Path child) {
        if (root == null) {
            return child;
        } else {
            return Paths.get(root.toString(), child.toString());
        }
    }

    /**
     * Writes a given directory or file to a zip file through a zipOutputStream
     *
     * @param out - The zipOutputStream that writes a file or folder to a zip
     * file
     * @param zipDir - the zip directory path to write to
     * @param dir - the regular directory or file to read from
     * @throws IOException
     */
    private static void addZipDir(final ZipOutputStream out, final Path zipDir, final Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path child : stream) {
                Path entry = buildPath(zipDir, child.getFileName());
                if (Files.isDirectory(child)) {
                    addZipDir(out, entry, child);
                } else {
                    out.putNextEntry(new ZipEntry(entry.toString()));
                    Files.copy(child, out);
                    out.closeEntry();
                }
            }
        }
    }

    /**
     * Compress the given path to a zip
     *
     * @param path
     * @throws IOException
     */
    public static void zipDir(final Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory.");
        }

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path.toString() + ".zip"));

        try (ZipOutputStream out = new ZipOutputStream(bos)) {
            addZipDir(out, path.getFileName(), path);
        }
    }

    /**
     * Unzip a zip file
     *
     * @param zipFile input zip file
     * @param outputFolder regular file output folder/directory
     */
    public static void unZipIt(Path zipFile, Path outputFolder) {

        byte[] buffer = new byte[1024];

        //get the zip file content
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toString()))) {

            //creator output directory is not exists
            File folder = new File(outputFolder.toString());
            if (!folder.exists()) {
                folder.mkdir();
            }

            addFromZipToRootFolder(zis, folder, buffer);

        } catch (IOException ex) {
        }
    }

    /**
     * Add individual zip entry to a destination folder
     *
     * @param zis - ZipInpustream created from a compressed zip file
     * @param outputFolder - File to output zip entry to
     * @param buffer - buffer to hold intermittent file content
     * @throws IOException
     */
    private static void addFromZipToFolder(ZipInputStream zis, File outputFolder, byte[] buffer) throws IOException {
        if (zis == null) {
            return;
        }

        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {

            String fileName = ze.getName();
            File newFile = new File(outputFolder + File.separator + fileName);

            System.out.println("file unzip : " + newFile.getAbsoluteFile());

            //creator all non exists folders
            //else you will hit FileNotFoundException for compressed folder
            new File(newFile.getParent()).mkdirs();

            try (FileOutputStream fos = new FileOutputStream(newFile)) {
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }
            ze = zis.getNextEntry();
        }

        System.out.println("Done");
    }

    /**
     * Add individual zip entry to root folder
     *
     * @param zis - ZipInpustream created from a compressed zip file
     * @param outputFolder - File to output zip entry to
     * @param buffer - buffer to hold intermittent file content
     * @throws IOException
     */
    private static void addFromZipToRootFolder(ZipInputStream zis, File outputFolder, byte[] buffer) throws IOException {
        if (zis == null) {
            return;
        }

        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {

            String fileName = ze.getName();
            String[] arr = fileName.split("/");
            fileName = arr[arr.length - 1];
            File newFile = new File(outputFolder + File.separator + fileName);

            System.out.println("file unzip : " + newFile.getAbsoluteFile());

            //creator all non exists folders
            //else you will hit FileNotFoundException for compressed folder
            new File(newFile.getParent()).mkdirs();

            try (FileOutputStream fos = new FileOutputStream(newFile)) {
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }
            ze = zis.getNextEntry();
        }

        System.out.println("Done");

    }


    public static Optional<String> getMd5ForFile(Path path) {
        //File myFile = new File(fileName);
        return getMd5ForFile(path.toFile());
    }

    public static Optional<String> getMd5ForFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return getMd5ForFile(fileInputStream);
        } catch (IOException e) {
            System.out.println("Exp: " + e.getMessage());
            return Optional.empty();
        }

    }

    public static Optional<String> getMd5ForFile(InputStream inputStream) {
        String md5Value = null;

        try {
            // md5Hex converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
            // The returned array will be double the length of the passed array, as it takes two characters to represent any given byte.
            md5Value = DigestUtils.md5Hex(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            LOGGER.info("Error occurred while Generating md5 Hash file: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return Optional.ofNullable(md5Value);
    }

    public static Optional<String> formatPhone(String phone) {
        String formattedPhone;
        if (StringUtils.isBlank(phone)) return Optional.empty();
        phone = phone.trim().replaceAll(" ", "").replaceAll("-", "");
        if (phone.startsWith("+234")) formattedPhone = phone.substring(1);
        else if (phone.startsWith("234")) formattedPhone = phone;
        else if (phone.startsWith("0")) formattedPhone = phone.replaceFirst("0", "234");
        else formattedPhone = "234" + phone;

        if (null != formattedPhone && formattedPhone.length() != 13) {
            throw new IllegalArgumentException("Formatted phone number must be 13 characters");
        }

        return Optional.ofNullable(formattedPhone);
    }

    public static String getFileExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) throw new IllegalArgumentException("File name should not be null");
        int i = fileName.lastIndexOf('.');
        return (i > 0) ? fileName.substring(i + 1) : "";
    }

    public static <T> T map(Object source, Class<T> destinationType) throws IllegalArgumentException {
        return source != null ? MODEL_MAPPER.map(source, destinationType) : null;
    }

    public static void  map(Object source, Object destination) throws IllegalArgumentException {
        MODEL_MAPPER.map(source, destination);
    }

    public static <T, S> Optional<T> mapOptional(Optional<S> source, Class<T> destinationType) throws IllegalArgumentException {
        return source.map(o -> MODEL_MAPPER.map(o, destinationType));
    }

    public static <T, R> List<T> map(List<R> sourceList, Class<T> clazz) throws IllegalArgumentException {
        if (sourceList == null) return Collections.emptyList();
        return sourceList.stream().map(x -> map(x, clazz)).collect(Collectors.toList());
    }

    public static <T, R> Set<T> map(Set<R> sourceList, Class<T> clazz) throws IllegalArgumentException {
        if (sourceList == null) return Collections.emptySet();
        return sourceList.stream().map(x -> map(x, clazz)).collect(Collectors.toSet());
    }

    public static <T> List<T> filterObjects(List<T> objects, Predicate<T> predicate) {
        return objects.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, S> List<S> filterAndMapObject(List<T> objects, Predicate<T> predicate, Class<S> destination) {
        return map(objects.stream().filter(predicate).collect(Collectors.toList()), destination);
    }

    public static <T, S> List<S> filterAndMapObject(List<T> objects, Predicate<T> predicate, Function<? super T, S> mapper) {
        return objects.stream().filter(predicate).map(mapper).collect(Collectors.toList());
    }


    private static PageRequest getPageRequest(Param p) {
        return getPageRequest(p, Sort.Direction.DESC, "createdAt" );
    }

    private static PageRequest getPageRequest(Param p, Sort.Direction sort, String... keys) {
        return PageRequest.of(p.getPage(), p.getSize(), sort, keys);
    }

    public static PageRequest getPageRequest(int page, int size) {
        return PageRequest.of(page < 0 ? 0 : page, size < 1 ? 50 : size, Sort.Direction.DESC, "createdAt");
    }

    public static PageRequest getPageRequest(int page, int size, Sort.Direction sort, String... keys) {
        return PageRequest.of(page < 0 ? 0 : page, size < 1 ? 50 : size, sort, keys);
    }

    public static PageRequest getPageRequest(HttpServletRequest request, Sort.Direction sort, String... keys) {
        return getPageRequest(getParam(request), sort, keys );
    }

    public static PageRequest getPageRequest(HttpServletRequest request) {
        return getPageRequest(getParam(request) );
    }

    public static <S,T> Page<T> map(Page<S> page, Class<T> clazz) {
        if (page == null) throw new IllegalArgumentException("Page object can not be null");
        return page.map(x -> map(x,clazz));
    }

    public static <T extends Identifiable<ID>, ID extends Serializable> T setIdAndGet(Long id, Permission model) {
        model.setId(id);
        return (T) model;
    }

    public static <T, ID> T newInstanceWithId(Class<T> model, ID id) {
        if (id == null) return null;
        T object = newInstance(model);
        setId(object, model, id);
        return object;
    }

    public static <T, ID> T map(Class<T> model, ID id) {
        return newInstanceWithId(model, id);
    }


    public static <T, ID> T getInstance(Class<T> model, ID id) {
        if (id == null){
            return null;
        }

        return Accessor.findOne(model, id instanceof Long ? (Long)id : (String)id);
        //setId(object, model, id);
        //return object;
    }

    public static <T> T newInstance(Class<T> model) {
        try {
            Constructor<T> constructor = model.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalArgumentException("Could not instantiate class " + model.getName());
        }
    }

    public static <T, ID> List<T> getEntitiesFromIds(Class<T> model, List<ID> ids) {
        return ids == null ? new ArrayList<>() : ids.stream().map(id -> newInstanceWithId(model, id)).collect(Collectors.toList());
    }

    public static <T, ID> Set<T> getEntities(Class<T> model, List<ID> ids) {
        return ids == null ? new HashSet<>() : ids.stream().map(id -> newInstanceWithId(model, id)).collect(Collectors.toSet());
    }

    public static <T, ID> Set<T> map(Class<T> model, List<ID> ids) {
        return getEntities(model, ids);
    }

    public static <T, ID> T setId(T object, Class<T> model, ID id) {
        Class clazz = id instanceof Long ? Long.class : String.class;
        try {
            Method s = model.getMethod("setId", clazz);
            s.invoke(object, id);
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOGGER.warn("Error while setting id for {}", model.getSimpleName());
        }
        return object;
    }

    public static <T extends Identifiable<ID>, ID extends Serializable> List<T> removeAllById(List<T> values, List<T> remove){
        for (T aRemove : remove) removeById(values, aRemove);
        return values;
    }

    public static <T extends Identifiable<ID>, ID extends Serializable> List<T> removeById(List<T> values, T toBeRemoved){
        for (int i = 0; i < values.size(); i++) {
            if (Objects.equals(values.get(i).getId(), toBeRemoved.getId()))
                values.remove(values.get(i));
        }
        return values;
    }

    public static String getIpAddress() throws Exception{
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static String getMcAddress() throws Exception{
        byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        return IntStream
                .range(0, mac.length)
                .mapToObj(i -> String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""))
                .collect(Collectors.joining());
    }

    public static double getProcessCpuLoad() {
        double cpuUsage = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad();
        return ((int)(cpuUsage * 1000))/10.0;
    }

    public static double getSystemCpuLoad() {
        double cpuUsage = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad();
        return ((int)(cpuUsage * 1000))/10.0;
    }

    public static long getTotalPhysicalMemorySize() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    }

    public static long getFreePhysicalMemorySize() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getFreePhysicalMemorySize();
    }

    public static Date getDateFromString(String date) {
        LocalDateTime parsedDate = toLocalDateTime(date);
        if (parsedDate == null) return null;
        return Date.from(parsedDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime toLocalDateTime(String date) {
        if (StringUtils.isEmpty(date)) return null;
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
    }


    public static Date parseDate(String date, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(date);
    }
}
