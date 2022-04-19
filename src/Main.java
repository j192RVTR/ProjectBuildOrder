import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter project list:");
        String projectLine = scanner.nextLine();
        System.out.println("Enter dependencies:");
        String dependencyLine = scanner.nextLine();

        if(projectLine.contains(":")){
            //projectLine = projectLine.split(":")[1].trim();
            projectLine = projectLine.substring(projectLine.indexOf(':')+1);
        }
        if(dependencyLine.contains(":")){
            //dependencyLine = dependencyLine.split(":")[1].trim();
            dependencyLine = dependencyLine.substring(dependencyLine.indexOf(':') +1);
        }
        String[] projects = projectLine.split(",");
        for(int i=0; i<projects.length; i++){
            projects[i] = projects[i].trim();
        }
        String[] raw = dependencyLine.split(",");

        List<String[]> dependencies = new ArrayList<>();
        for(int i=0; i<raw.length - 1; i++){
            if(raw[i].trim().charAt(0) == '(' && raw[i+1].charAt(raw[i+1].length()-1) == ')'){
                String[] dep = new String[2];
                dep[0] = raw[i].trim().substring(1);
                dep[1] = raw[i+1].substring(0, raw[i+1].length()-1).trim();
                dependencies.add(dep);
            }
        }

        System.out.println(getBuildOrder(projects, dependencies));
    }

    public static String getBuildOrder(String[] projects, List<String[]> dependencies){
        List<String> built = new ArrayList<>();
        List<String> proj_list = Arrays.asList(projects);
        while(built.size()<projects.length) {
            boolean fail = true;
            int[] num_dep = new int[projects.length];

            for(String[] dep : dependencies){
                if(!built.contains(dep[0])){
                    int index = proj_list.indexOf(dep[1]);
                    //A Dependency for a project not defined can safely be ignored when generating build order.
                    //The reverse case of a dependency on a project not defined will always lead to an ERROR.
                    if(index<0)
                        continue;
                    num_dep[index]++;
                }
            }
            for(int i=0; i<num_dep.length; i++){
                if(num_dep[i] == 0 && !built.contains(proj_list.get(i))){
                    built.add(proj_list.get(i));
                    fail = false;
                }
            }
            if(fail)
                break;
        }

        if(proj_list.size()==0){
            return "";
        }

        if(built.size() < proj_list.size())
            return "ERROR";

        //Possible to have failing dependencies defined, so should go after failure case.
        if(proj_list.size()==1){
            return proj_list.get(0);
        }

        StringBuilder sb = new StringBuilder();
        for(String s: built){
            sb.append(s).append(", ");
        }

        return sb.substring(0, sb.length()-2);
    }
}
