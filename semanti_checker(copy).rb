require 'nokogiri'
require 'colorize'

class Var
  def initialize(name, type) 
    @name = name
    @type = type
  end
  @name
  @type
  attr_accessor :name, :type
  def to_s 
    "#{@type} #{@name}"
    
  end

end

class Fun
  def initialize(name, type, parameters)
    @name = name
    @type = type
    @parameters = parameters
  end
  attr_accessor :name, :type, :parameters
  def to_s 
    out = "#{@type}\n#{@name}\n"
    @parameters.each do |p|
      out << "\t#{p.to_s}\n"
    end
    out << "\n"
  end
end

@types = %w(INT FLOAT STRING CHAR BOOL )
@functions = []
@variables = []

def check_return (block)
  if block.at_xpath('RETURN')
    return;
  else
 #   puts 'function ' + block.at_xpath('../IDENTIFIER')['src'] + ' is missing ' + 'return' + ' statement'
  end
end

def exp_check (element, type = '')
 # puts 'exp that should return ' + type.blue + ' to ' + element.parent.name
end

#put inside for
def exp_check2 (element)
  # puts element
  @same_type = nil
  @prev_var = nil
  element.xpath('*').each do |e|
    if (type = is_variable?(e['src']))
      unless @same_type
        @same_type = type
        @prev_var = e['src']
      else
        if @same_type != type
          puts @prev_var + ' and ' + e['src'] + ' types should be same(' + @same_type + ' ' + type + ' )'
        end
      end
    else
      puts e['src'] + ' is not defined'
      break
    end
  end
end

#TODO not tested
def is_function? (name)
  return false if @functions == []
  @functions.each do |f|
    return f.parameters if f.name == name
  end
  return false
end

def add_function(f)
  param = f.xpath('PARAMETER/TYPE').map do |var|
    Var.new(var.at_xpath('IDENTIFIER')['src'],
                  var['class'])
  end
  depth = f.ancestors.count
  @variables[depth] ||= []; @variables[depth] += param
  unless is_function?(f.at_xpath('IDENTIFIER')['src'])
    @functions << Fun.new(f.at_xpath('IDENTIFIER')['src'],
                          f.at_xpath('TYPE')['class'],
                          param)
  else
    puts 'function ' + f.at_xpath('IDENTIFIER')['src'] + ' already exists'
  end
end

#TODO not tested
def is_variable? (name)
  return false if @variables == []
  @variables.each do |depth|
    next if depth.nil?
    depth.each do |var|
      return var.type if var.name == name
    end
  end
  return false
end

def add_variable(v, t = '')# t tam atvejui kai kintamasis nera explicit defined
  depth = v.ancestors.count
  unless is_variable?(v.at_xpath('IDENTIFIER')['src'])
    @variables[depth] ||= []
    if t == ''
    @variables[depth] << Var.new(v.at_xpath('IDENTIFIER')['src'],
                          v['class'])
    else
    @variables[depth] << Var.new(v.at_xpath('IDENTIFIER')['src'],
                          t)
    end
  else
    puts 'variable ' + v.at_xpath('IDENTIFIER')['src'] + ' already exists'
  end
end

def assig_check(element)
  if element.at_xpath('./*[1]').name == 'TYPE'
    add_variable(element.at_xpath('TYPE'))
    #exp_check(element.at_xpath('./EXPRESSION'), element.at_xpath('TYPE')['class'])
  else
    exp_check(element.at_xpath('./EXPRESSION'))
  end
end

def funcal_check(element)
  if (par = is_function?(element.at_xpath('IDENTIFIER')['src']))
    if element.xpath('EXPRESSION').count == par.count
      element.xpath('EXPRESSION').each.with_index do |e, i|
        exp_check(e, par[i].type)
      end
    else
      puts 'function ' + element.at_xpath('IDENTIFIER')['src'] + ' needs ' + par_c.to_s + ' arguments, got ' + element.xpath('EXPRESSION').count.to_s
    end
  else
    puts 'declaration of function ' + element.at_xpath('IDENTIFIER')['src'] + ' not found'
  end
end

def while_check(element)
  #deal with expresion
  exp_check2(element.at_xpath('*[1]'))
  check_block(element.at_xpath('BLOCK'))
end

def for_check(element)
  @same_type = nil
  @prev_var = nil
  element.xpath('ASSIG/TO/IDENTIFIER').each do |e|
    if (type = is_variable?(e['src']))
      unless @same_type
        @same_type = type
        @prev_var = e['src']
      else
        unless @same_type == type
          puts @prev_var + ' and ' + e + ' types should be same(' + @same_type + ' ' + type + ' )'
        end
      end
    else
      puts e['src'] + ' is not defined'
      break
    end
  end
  add_variable(element.at_xpath('ASSIG'), @same_type)
  check_block(element.at_xpath('BLOCK'))
  @variables.pop(@variables.size - element.ancestors.count).to_s
end

#TODO test
def if_check(element)
  exp_check2(element.at_xpath('*[1]'))
  check_block(element.at_xpath('BLOCK'))
  if (el = element.at_xpath('ELSE'))
    check_block(el.at_xpath('BLOCK'))
  end
end

def sout_check(element)
  element.xpath('EXPRESSION').each { |e| exp_check(e)}
end

#TODO test
def sin_check(element)
  if (type = is_variable?(element.at_xpath('IDENTIFIER')['src']))
    unless type == 'STRING'
      puts "systemIn needs argument of type " + "string" + " got" + type
    end
  else
    puts "systemIn got undefined argument " + element.at_xpath('IDENTIFIER')['src']
  end
end

def check_block (block)
  block.xpath('*').each do |element|
    case element.name
    when 'ASSIG'
      assig_check(element)
    when 'FUNCAL'
      funcal_check(element)
    when 'TYPE'
      add_variable(element)
    when 'WHILE'
      while_check(element)
    when 'FOR'
      for_check(element)
    when 'IF'
      if_check(element)
    when 'SYSTEMIN'
      sin_check(element)
    when 'SYSTEMOUT'
      sout_check(element)
    when 'RETURN'
      element.xpath('EXPRESSION').each do |e| 
        if t = e.at_xpath('../../TYPE')
          exp_check(e, t['class'])
        else
          exp_check(e)
        end
      end
    else
      puts 'unhandled statement: ' + element.name.yellow
    end
  end
  @variables.pop(@variables.size - block.ancestors.count).to_s
end

def check_function (fun_defs)
  fun_defs.each do |f|
    # puts f.name
    add_function(f)
    check_return(f.at_xpath('BLOCK')) if f.at_xpath('TYPE')
    check_block(f.at_xpath('BLOCK'))
    #p @variables
    @variables.clear
  end 
  #puts @functions
end

def check_main (main_def)
  # puts main_def.name
  check_block(main_def.at_xpath('BLOCK'))
end
#compare fun table with funcall
def check_funcall (f)
end

def patch (doc)
  @types.each do |type|
    doc.xpath("//#{type}").each do |i| 
      i.name = 'TYPE'
      i['class'] = type
    end
  end
  doc
end

doc = File.open('logfile.xml') { |f| Nokogiri::XML(f) }
root = patch(doc.root)

if (!(f = root.xpath('FUNCTION')).empty?)
  #p f
  check_function(f)
  #check_main(f)
else
  puts 'No function definition'
end
check_main(root.at_xpath('MAIN'))
#Output
doc = doc.to_xml( indent:2).gsub(/(\s*((<\/)|(<\?)).*)|[<>\/]/,'')
doc = doc.gsub(/(src|class)="(.+?)"/) {|s| s = $2}
File.write('output.txt', doc)
