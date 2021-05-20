FROM ubuntu:latest
RUN apt update -y && apt install -y perl git cpanminus
COPY BioSD /root/BioSD
COPY dcc-validate-metadata /root/dcc-validate-metadata
RUN apt install -y libmodule-build-pluggable-perl
RUN apt install -y build-essential
RUN /usr/bin/perl /root/BioSD/Build.PL | cpanm
RUN cpanm --verbose /root/BioSD
RUN apt install -y libxml-parser-perl
RUN cpanm --verbose Spreadsheet::ParseXLSX
RUN cpanm --verbose REST::Client
RUN cpanm --verbose XML::Simple
RUN cpanm --verbose -n JSON::Validator@0.91
RUN cpanm --verbose -n /root/dcc-validate-metadata
ENTRYPOINT ["perl", "/root/dcc-validate-metadata/scripts/validate_rulesets_local.pl"]

