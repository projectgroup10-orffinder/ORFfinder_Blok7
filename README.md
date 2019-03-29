THIS IS THE MASTER BRANCH
# ORFfinder_blok7
Version 1.0
features:
-GUI
-Can open fasta files with 1 DNA sequence in it. DNA sequence of up to 240016 base pairs has been tested.
-Can find ORFs based on a reading frame of at least 75 base pairs long.
-Can find ORFs between start codon and stop codons or between 2 stop codons.
-Found ORFs can be locally saved and are uploaded to the database automatically upon analysing.

Known bugs:
-Can only locally save files on Windows, trying to save a file on Linux will result in an error or your file not being saved to the correct path. Other functions will work in Linux (Ubuntu).
-Uploading to the database may take a while with longer sequences.
-Will abbort uploading the data to database when the hascode of the original DNA sequence is already present in the database or the hascode of the DNA sequence of a detected ORF + its location is already present in the database.