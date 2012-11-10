package perf;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.avro.AvroFactory;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;

abstract class PerfBase
{
    // From jvm-serializers tests
    protected final static String JVM_SERIALIZERS_SCHEMA_STR =
"{\"type\":\"record\",\"name\":\"MediaContent\",\n"
+"\"namespace\":\"serializers.avro.media\","
+"\"fields\":[\n"
+"{\"name\":\"images\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Image\",\"fields\":[{\"name\":\"uri\",\"type\":\"string\"},{\"name\":\"title\",\"type\":[\"null\",\"string\"]},{\"name\":\"width\",\"type\":\"int\"},{\"name\":\"height\",\"type\":\"int\"},{\"name\":\"size\",\"type\":\"int\"}]}}},"
+"{\"name\":\"media\",\"type\":{"
+"  \"type\":\"record\",\"name\":\"Media\",\n"
+  "\"fields\":[{\"name\":\"uri\",\"type\":\"string\"},\n"
+"   {\"name\":\"title\",\"type\":[\"null\",\"string\"]},\n"
+"   {\"name\":\"width\",\"type\":\"int\"},\n"
+"   {\"name\":\"height\",\"type\":\"int\"},\n"
+"   {\"name\":\"format\",\"type\":\"string\"},\n"
+"   {\"name\":\"duration\",\"type\":\"long\"},\n"
+"   {\"name\":\"size\",\"type\":\"long\"},\n"
+"   {\"name\":\"bitrate\",\"type\":[\"null\",\"int\"]},\n"
+"   {\"name\":\"persons\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},\n"
+"   {\"name\":\"player\",\"type\":\"int\"},\n"
+"   {\"name\":\"copyright\",\"type\":[\"null\",\"string\"]}"
+  "]}"
+"}"
+"]}"
            ;

    public static class MediaItem
    {
         public Media media;
         public List<Image> images;

         public MediaItem addPhoto(Image i) {
             if (images == null) {
                 images = new ArrayList<Image>();
             }
             images.add(i);
             return this;
         }
    }

    public enum Size { SMALL, LARGE };
    
    public static class Image
    {
        public Image() { }
        public Image(String uri, String title, int w, int h, Size s) {
            this.uri = uri;
            this.title = title;
            width = w;
            height = h;
            size = s;
        }

        public String uri;
        public String title;
        public int width, height;
        public Size size;    
    } 
    
    public enum Player { JAVA, FLASH; }

    public static class Media {

        public String uri;
        public String title;        // Can be unset.
        public int width;
        public int height;
        public String format;
        public long duration;
        public long size;
        public int bitrate;         // Can be unset.
//        public boolean hasBitrate;

        public List<String> persons;
        
        public Player player;

        public String copyright;    // Can be unset.    

        public Media addPerson(String p) {
            if (persons == null) {
                persons = new ArrayList<String>();
            }
            persons.add(p);
            return this;
        }
    }

    protected AvroSchema parseSchema() {
        return new AvroSchema(new Schema.Parser().setValidate(true).parse(JVM_SERIALIZERS_SCHEMA_STR));        
    }

    protected ObjectMapper avroMapper() {
        ObjectMapper mapper =  new ObjectMapper(new AvroFactory());
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
        return mapper;
    }
    
    protected ObjectReader avroReader(Class<?> type) {
        return avroMapper()
                .reader(type)
                .with(parseSchema());
    }
 
    protected ObjectWriter avroWriter(Class<?> type) {
        return avroMapper()
                .writerWithType(type)
                .withSchema(parseSchema());
    }
    
    protected MediaItem buildItem()
    {
        Media content = new Media();
        content.player = Player.JAVA;
        content.uri = "http://javaone.com/keynote.mpg";
        content.title = "Javaone Keynote";
        content.width = 640;
        content.height = 480;
        content.format = "video/mpeg4";
        content.duration = 18000000L;
        content.size = 58982400L;
        content.bitrate = 262144;
        content.copyright = "None";
        content.addPerson("Bill Gates");
        content.addPerson("Steve Jobs");

        MediaItem item = new MediaItem();
        item.media = content;

        item.addPhoto(new Image("http://javaone.com/keynote_large.jpg", "Javaone Keynote", 1024, 768, Size.LARGE));
        item.addPhoto(new Image("http://javaone.com/keynote_small.jpg", "Javaone Keynote", 320, 240, Size.SMALL));

        return item;
    }
    
}