import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hongzhan on 2018/6/30.
 */
public class Main {
    int totalOrder=5;
    Map<String,List<String>>[] orderMarkov=new HashMap[totalOrder];
    Map<String,List<String>>[] reverseMarkov=new HashMap[totalOrder];
    Map<String,String>wordNature=new HashMap<>();
    Random random=new Random();
    int oneSideLength=50;

    public void init(){
        for(int i=0;i<totalOrder;i++){
            orderMarkov[i]=new HashMap<>();
            reverseMarkov[i]=new HashMap<>();
        }

        try {
//            InputStreamReader isr = new InputStreamReader(Main.class.getResourceAsStream("Noruuenomori.txt"), "gbk");
            InputStreamReader isr = new InputStreamReader(Main.class.getResourceAsStream("hlm.txt"), "gbk");

            BufferedReader in = new BufferedReader(isr);
            String line;
            int lineCount=0;

            while((line = in.readLine()) != null){
                line=line.trim();
                if(!line.isEmpty()) {
//                    System.out.println(line);
                    line=line.replace("！","。").replace("？","。");
                    line="。"+line;
//                    for(String sentence:line.split("。")) {
                    List<Term> terms = ToAnalysis.parse(line).getTerms();
                    if(terms.size()<3)
                        continue;
                    fillMap(terms,orderMarkov);
                    Collections.reverse(terms);
                    fillMap(terms,reverseMarkov);

//                    }
                    lineCount++;

                }
            }
            in.close();
            System.out.println(lineCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillMapCharacter(List<Term> terms,Map<String,List<String>>[] markov) {
        String s = String.join("", terms.stream().map(u -> u.getName()).collect(Collectors.toList()));
        for(int order=1;order<=totalOrder;order++){
            Map<String, List<String>> map = markov[order - 1];
            for(int i=0;i<s.length()-order;i++){
                String key="";
                for(int keyGen=i;keyGen<i+order;keyGen++){
                    key+=s.charAt(keyGen);
                }
                if(!map.containsKey(key)){
                    map.put(key,new ArrayList<>());
                }
                map.get(key).add(""+s.charAt(i+order));
            }
        }
    }

    private void fillMap(List<Term> terms,Map<String,List<String>>[] markov) {
        for(int order=1;order<=totalOrder;order++){
            Map<String, List<String>> map = markov[order - 1];
            for(int i=0;i<terms.size()-order;i++){
                String key="";
                for(int keyGen=i;keyGen<i+order;keyGen++){
                    key+=terms.get(keyGen).getName();
                }
                if(!map.containsKey(key)){
                    map.put(key,new ArrayList<>());
                }
                map.get(key).add(terms.get(i+order).getName());
            }
        }
    }

    public String gen(String s){
      List<String>ret=new LinkedList<>();

        ret.addAll(ToAnalysis.parse(s).getTerms().stream().map(u->u.getName()).collect(Collectors.toList()));
        boolean over=false;
        while (ret.size()<oneSideLength && !over) {
            over = genCore(ret, over,true);
        }
        over=false;
        Collections.reverse(ret);
        while (ret.size()<oneSideLength*2 && !over) {
            over = genCore(ret, over,false);
        }
        Collections.reverse(ret);
        return String.join("",ret);
    }

    private boolean genCore(List<String> ret, boolean over,boolean append) {
        List<String>pool=new ArrayList<>();
        for (int i = totalOrder - 1; i >= 0; i--) {
            Map<String, List<String>> markov = append?orderMarkov[i]:reverseMarkov[i];
            if (ret.size() > i) {
               List<String>keys=new ArrayList<>();
                for (int keyGen = 0; keyGen <= i; keyGen++) {
                    keys.add( ret.get(ret.size() - keyGen - 1));
                }
               Collections.reverse( keys);
                String key=String.join("",keys);
                if(markov.containsKey(key)) {
                   int dicSize = markov.get(key).size();
                    ret.add(markov.get(key).get(random.nextInt(dicSize)));
                    break;
                }
            }
            if(i==0){
                over=true;
            }
        }

        return over;
    }

    public String gen(){
        Map<String, List<String>> markovO2 = orderMarkov[totalOrder-1];
        String start= markovO2.keySet().stream().collect(Collectors.toList()).get(random.nextInt(markovO2.keySet().size()));
        return gen(start);
    }

    public static void main(String[] args) {

        Main m=new Main();
        m.init();
        Set<String>ret=new HashSet<>();
        String center="妙玉听如此说";
        for(int i=0;i<5000;i++) {
            String gen = m.gen(center);
            int centerIndex=gen.indexOf(center);

            int fstPeriod=gen.indexOf("。",centerIndex);
            int sndPeriod=gen.indexOf("。", fstPeriod+1);
            int start=gen.substring(0,centerIndex).lastIndexOf("。");
            if(fstPeriod>-1 && start<fstPeriod){
                ret.add(gen.substring(start+1,fstPeriod).trim());

            }
        }
        ret.stream()

                .sorted().forEach(u-> {
                System.out.println(u);});

    }
}
