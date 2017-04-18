# Bochert

Bochert is a multi-threaded new method to solve nP complete problems in Java. It is set up to read .clq files and fine the exact solution of the max clique.

Working directory (called NewMaxClique) is currently set up for use in eclipse

Installation
----
You can download source files via git clone, or just grab the compiled .jar files and accompanying class directory

Usage
----
To get a list of basic options and switches, use:

java -jar bochert.jar -h

usage: bochert [options]
A multi-threaded, infinitely scalable solution to find an exact max clique
               from a graph.clq (nP problem-set solution)

 -d,--directory <arg>   directory for graph binaries, default is
                        "../graph_binaries"
 -e,--exclude <arg>     graphs to exclude. Default is
                        "MANN_a45.clq,MANN_a81.clq,keller5.clq,keller6.clq
                        "
 -g,--graphs <arg>      .clq graphs to run, default is all graphs in
                        directory of graph binaries. Can be comma
                        delimiated list, eg
                        "brock200_1.clq,brock200_2.clq,brock200_3.clq,broc
                        k200_4.clq"
 -h,--help              this menu
 -m,--max <arg>         display incrimental max as it's found, default is
                        true
 -o,--other <arg>       use other algorithm to find max Clique. Options
                        include:
                        BK (BronKerbosch with pivot)
                        MC (Tomiata's MC)
                        MC0 (Tomiata's MC)
                        MCQ1 (Tomiata's MCQ style 1)
                        MCQ2 (Tomiata's MCQ style 2)
                        MCQ3 (Tomiata's MCQ style 3)
                        MCSa1 (Tomiata's MCSa style 1)
                        MCSa2 (Tomiata's MCSa style 2)
                        MCSa3 (Tomiata's MCSa style 3)
                        MCSb1 (Tomiata's MCSb style 1)
                        MCSb2 (Tomiata's MCSb style 2)
                        MCSb3 (Tomiata's MCSb style 3)
                        BBMC1 (San Segundo's BBMC style 1)
                        BBMC2 (San Segundo's BBMC style 1)
                        BBMC3 (San Segundo's BBMC style 1)
 -t,--threads <arg>     threads to allow, default is 24. Use 0 to disable
                        multithreding
 -v,--verbosity <arg>   set verbosity, -2 minimal display, -1 display
                        graph meta no algorithm out, 0 graph meta and
                        algorithm display, 1 >= increasing algorithm
                        verbosity level, default -1

Examples
----
