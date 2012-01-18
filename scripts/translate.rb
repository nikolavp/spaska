#!/usr/bin/ruby
# vim: set sw=4 sts=4 et tw=80 :

#Used to test a markdown page. This accepts a single argument - the file to be
#processed by markdown.
require 'redcarpet/compat'
puts Markdown.new(File.read(ARGV[0])).to_html
