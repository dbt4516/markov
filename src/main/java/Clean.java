import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hongzhan on 2019/12/1.
 */
public class Clean {
    public static String smartCut(String s){
        int endIndex=-1;
        int startIndex=-1;
        for(int i=s.length()-1;i>=0;i--){
            if(isChinese(s.charAt(i))){
                endIndex=i;
                break;
            }
        }
        for(int i=0;i<s.length();i++){
            if(isChinese(s.charAt(i))){
                startIndex=i;
                break;
            }
        }
        if(startIndex==-1 || endIndex==-1){
            return "";
        }
        return s.substring(startIndex,endIndex+1);
    }

    public static boolean isChinese(char c){
        int n = 0;
        if("“。”）".contains(c+"")){
            return true;
        }

            n = (int)c;
            if(!(19968 <= n && n <40869)) {
                return false;
            }

        return true;
    }

    private static List<String>bw=new LinkedList<>();

    static {
        String raw ="下载次数\n" +
                "下载附件\n" +
                "保存到相册\n" +
                "由手机上传\n" +
                "上传";
        for(String s:raw.split("\n")){
            bw.add(s);
        }
    }


    public static void main(String[] args) throws IOException {

        List<String> ss = FileUtils.readLines(new File(Clean.class.getClassLoader().getResource("x.txt").getFile()),"utf-8");
        List<String>ret=new LinkedList<>();
        for(String raw:ss){
            String s=smartCut(raw).trim();
            if(s.contains("本帖最后由")||s.contains("发表于")||bw.stream().filter(u->u.equals(s)).count()>0){
                continue;
            }
            if(!s.isEmpty()){

                    ret.add(s);
            }
        }
        FileUtils.writeLines(new File("out.txt"),ret,"\n");
    }
}
