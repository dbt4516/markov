import info.monitorenter.cpdetector.io.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Transcode {

    public static void main(String[] args) throws Exception {
        Collection<File> files = FileUtils.listFiles(new File("/Volumes/Untitled 1/spe/T"), new String[]{"txt"}, false);
        for(File f:files){

            if(f.getName().startsWith(".")){
                continue;
            }
            String fileCharset = getFileCharset(f.getPath());
            System.out.println(f.getName()+" "+fileCharset);

            if(fileCharset.equals("UTF-16LE")){
                fileCharset="UTF-8";
            }
            if(fileCharset.equals("ISO_8859_1")){
                fileCharset="GBK";
            }
            List<String> ss = FileUtils.readLines(f,fileCharset);

           if( String.join(" ",ss).contains("�")){
               ss = FileUtils.readLines(f,"GBK");
           }
           if(!fileCharset.equals("GBK")){
               continue;
           }
            ss=ss.stream().map(s->{
                List<String>ret=new LinkedList<>();
               for(int i=0;i<=s.length()/400;i++){
                   String trim = s.substring(i * 400, Math.min((i + 1) * 400, s.length())).trim();
                   if(trim.length()>5){
                       ret.add(trim);
                   }
               }
                try {
//                    FileUtils.writeLines(new File("/Volumes/Untitled 1/spe/markov/src/main/resources/x.txt"),ret,"\n",true);
                    FileUtils.writeLines(new File(String.format("/Volumes/Untitled 1/spe/markov/src/main/resources/%s",f.getName())),ret,"\n",true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return ret;
            })  .flatMap(List::stream).collect(Collectors.toList());

        }

    }

    public static String getFileCharset(String filePath) throws Exception {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*ParsingDetector可用于检查HTML、XML等文件或字符流的编码,
         * 构造方法中的参数用于指示是否显示探测过程的详细信息，为false不显示。
         */
        detector.add(new ParsingDetector(false));
        /*JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码测定。
         * 所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以再多加几个探测器，
         * 比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());
        detector.add(ASCIIDetector.getInstance());
        detector.add(UnicodeDetector.getInstance());
        Charset charset = null;
        File file = new File(filePath);
        try {
            //charset = detector.detectCodepage(file.toURI().toURL());
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            is.skip(800);
            charset = detector.detectCodepage(is, 8);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        String charsetName = "GBK";
        if (charset != null) {
            if (charset.name().equals("US-ASCII")) {
                charsetName = "ISO_8859_1";
            } else if (charset.name().startsWith("UTF")) {
                charsetName = charset.name();// 例如:UTF-8,UTF-16BE.
            }
        }
        return charsetName;
    }
}
