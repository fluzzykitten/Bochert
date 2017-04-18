# Bochert

Bochert is a multi-threaded new method to solve nP complete problems in Java. It is set up to read .clq files and find the exact solution of the max clique. It only finds one max clique, so if there are more cliques of equal size to the one found,  it won't tell you, but it will tell you that there exist no cliques larger than the one it found.

Yay for happy homegrown projects! This was an idea I had after gradschool for a superfast (hopefully) method to solve nP complete solutions. After I got it working, I made it multithreaded. It's project I worked on in my free time, in hopes that some day it'd be useful to people, let me know if you do find it useful, I'd appreciate knowing I didn't waste 3 or 4 years of my life =)

As far as the algorithm goes, it's not terible complicated at it's core, but I never bothered to publish anything. Suffice to say it uses magic, like a kitten... riding a unicorn... over a rainbow. If it turns out people actually care about the nuts and bolts, maybe some day I'll write a detailed analysis on the algorithm and how it's implimented. But if nobody cares about detials, then just know - kittens and rainbows.

Btw, Working directory (called NewMaxClique) is currently set up for use in eclipse

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

** note ** other algorithms do not use multithreading

Examples
----
java -jar bochert.jar
  same as:
java -jar bochert.jar -d ../graph_binaries -e "brock200_1.clq,MANN_a45.clq,MANN_a81.clq,keller5.clq,keller6.clq" -m true -t 24 -v -1
  These will run bochert algorithm against all graphs in graph.binaries directory excluding graphs specified

java -jar bochert.jar -g MANN_a27.clq
  run bochert against specified graph

java -jar bochert.jar -o mcq1 -g MANN_a27.clq
  this will run MCQ1 algorithm against graph specified (no other algorithms impliment multithreading)  
