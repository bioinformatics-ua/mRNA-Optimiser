mRNA-Optimiser
==============

Console application to redesign mRNA nucleotide sequences (given as text strings) to enhance its secondary structure in terms of minimum free energy

**Usage**: `mRNAoptimizer [*options*] nucleotide_sequence`

Example usages:

        **mRNAoptimizer** GUCACGUACUGACGUACUGCAGUCA
        **mRNAoptimizer** -f sequence_file.txt
        **mRNAoptimizer** -f sequence_file.txt -b 100 -e 300 -o output.txt

**Options**:



- `-f inputFile`      Give input sequence as a file.

- `-o outputFile`     Output results to file (Default = standard output).

- `-b index`          Index of the first nucleotide of the start codon (default=1)

- `-e index`          Index of the last nucleotide of the stop codon (default = sequence size)

- `-d type`           Optimization type: 0 to maximize MFE (default) / 1 to minimize MFE

- `-t maxTime`        Maximum optimization time, in minutes (default = no limit).

- `-i iterations`     Number of iterations the algorithm runs. The more iterations the longer it will take, but results will usually be better (default = 4000)

- `-c codingTable`    Genetic Code Table to use(default=1) according to this list:
                  [http://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi](http://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi "http://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi")

- `-q`                Don't output anything else than the resulting sequence.

- `-g`                Maintain the same GC content as the original sequence. With this option, the MFE optimization won't be as expressive.
