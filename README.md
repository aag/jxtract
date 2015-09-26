JXtract
=======

JXtract is a Java implementation of the Xtract tool described by Frank Smadja
in this 1993 ACL paper:
 
[Smadja, F. 1993. Retrieving collocations from text: Xtract. Comput. Linguist. 19, 1 (Mar. 1993), 143-177.](http://dl.acm.org/citation.cfm?id=972458&coll=Portal&dl=ACM)


JXtract accepts a word and a corpus as input, and it outputs collocations
found in the corpus containing the word. What's a collocation? A collocation
is defined as "recurrent combinations of words that co-occur more often than
expected by chance and that correspond to arbitrary word usages." Basically,
they're groups of words that often go together and usually mean something
different when together than when they're apart. Examples are
"The United Nations" and "Natural Language Processing".

The output looks like this:

    _ _ _ _ the european central bank _ _ _
    _ _ _ _ _ european food safety agency _ _
    _ _ _ _ the european union _ _ _ _ 
    _ _ _ _ the european court of _ _ _
    group of the party of european socialists _ _ _ _

Each underscore is a variable word. The outside underscores can generally be
cut off, then you are (hopefully) left with collocations.

JXtract was implemented as part of a grad school semester project. More
information about that project is available at
[the project page](http://definingterms.com/projects/Champollion/).

Limitations
-----------
The original Xtract uses a Part of Speech tagger and a parser to both
increase accuracy and create phrasal templates. JXtract is self contained,
and does not have these features.


Usage
-----
After cloning the repository, you'll need to build the software. JXtract uses
Gradle Wrapper, so you can build everything with this command:

    $ ./gradlew installDist

You'll need a text file with English words as input to JXtract. Development was
done with a file from the [Europarl Corpus](http://www.statmt.org/europarl/).

Once you've built the software and you have an input file, you can run JXtract.
Running it without any arguments will print some help information:

    $ ./build/install/JXtract/bin/JXtract

To find collocations, you'll need to manually choose a word and JXtract will
find collocations containing that word. For example:

    $ ./build/install/JXtract/bin/JXtract -source ./ep-00-en.txt -word European


License
-------
This code is free software licensed under the GPL v3.  See the COPYING file
for details.
