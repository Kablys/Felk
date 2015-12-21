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
    out = "#{@type} #{@name}\n"
    @parameters.each do |p|
      out << "\t#{p.to_s}\n"
    end
    out << "\n"
  end
end

@error = false
@types = %w(INT FLOAT STRING CHAR BOOL VOID)
@values = %w(NUMBER FLOATNUMBER CHARSET)
@type_to_val = {'INT' => 'NUMBER',
                'FLOAT' => 'FLOATNUMBER',
                'STRING' => 'CHARSET'}
@functions = []
@variables = []
@all_variables = []

def check_return (block)
  if block.at_xpath('RETURN')
    return;
  else
    puts 'function ' + block.at_xpath('../IDENTIFIER')['src'].green + ' is missing ' + 'return'.red + ' statement'
    @error = true
  end
end

def exp_check (element, expected_type = '')
#  puts 'exp that should return ' + expected_type.blue + ' to ' + element.parent.name
  #  VALUE checking
  (@values - [@type_to_val[expected_type]]).each do |value|
    val = element.xpath(".//#{value}") - element.xpath(".//FUNCTIONCAL//#{value}")
    unless val.empty?
      val.each do |v|
        puts "expected values #{@type_to_val[expected_type].green}, got #{value.red}(#{v['src'].red})" 
        @error = true
      end
    end
  end
  # IDENTIFIER checking
  id = element.xpath('.//IDENTIFIER') - element.xpath('.//FUNCTIONCAL//IDENTIFIER')
  id.each do |i|
    if type = is_variable?(i['src'])
      unless type == expected_type
        puts 'variables must be of type ' + expected_type.green + ', but ' + i['src'].red + ' is of the type ' + type.red
        @error = true
      end
    else
      puts i['src'].red + ' is not defined in expression' 
      @error = true
    end
  end
  # FUNCAL checking
  fun = element.xpath('.//FUNCTIONCAL') - element.xpath('.//FUNCTIONCAL//FUNCTIONCAL')
  fun.each do |f|
    #puts expected_type
    #puts f
    typef = funcal_check(f)
    if typef == ''
      next
    elsif typef != expected_type
      puts expected_type.green
      puts f.at_xpath('IDENTIFIER')['src'].green + ' returns value of type ' + typef.red + ' but expected ' + expected_type.green
      @error = true
    end
  end
end

#put inside for
def exp_check2 (element)
  #puts element
  @same_type = nil
  @prev_var = nil
  element.xpath('*').each do |e|
    if (type = is_variable?(e['src']))
      unless @same_type
        @same_type = type
        @prev_var = e['src']
      else
        if @same_type != type
          puts @prev_va.green + ' and ' + e['src'].green + ' types should be same(' + @same_type.red + ' ' + type.red + ' )'
          @error = true
        end
      end
    else
      puts e['src'].red + ' is not defined'
      @error = true
      break
    end
  end
end

def is_function? (name)
  return false if @functions == []
  @functions.each do |f|
    return f if f.name == name
  end
  return false
end

def add_function(f)
  param = f.xpath('PARAMETER/TYPE').map do |var|
    Var.new(var.at_xpath('IDENTIFIER')['src'],
                  var['class'])
  end
  depth = f.ancestors.count
  @variables[depth] ||= []
  param.each do |p|
    @variables[depth] << p
  end
  unless is_function?(f.at_xpath('IDENTIFIER')['src'])
    @functions << Fun.new(f.at_xpath('IDENTIFIER')['src'],
                          f.at_xpath('TYPE')['class'],
                          param)
  else
    puts 'function ' + f.at_xpath('IDENTIFIER')['src'].green + ' already exists'
    @error = true
  end
end

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
    @all_variables << Var.new(v.at_xpath('IDENTIFIER')['src'],
                          v['class'])
    else
    @variables[depth] << Var.new(v.at_xpath('IDENTIFIER')['src'],
                          t)
    @all_variables << Var.new(v.at_xpath('IDENTIFIER')['src'],
                              t)
    end
  else
    puts 'variable ' + v.at_xpath('IDENTIFIER')['src'].green + ' already exists'
    @error = true
  end
end

def assig_check(element)
  if element.at_xpath('./*[1]').name == 'TYPE'
    add_variable(element.at_xpath('TYPE'))
    exp_check(element.at_xpath('./EXPRESSION'), element.at_xpath('TYPE')['class'])
  elsif type = is_variable?(element.at_xpath('IDENTIFIER')['src'])
    exp_check(element.at_xpath('./EXPRESSION'), type)
  else
    puts 'variable' + element.at_xpath('IDENTIFIER')['src'].red + 'is not defined in assignment'
    @error = true
  end
end

def funcal_check(element)
  if (f = is_function?(element.at_xpath('IDENTIFIER')['src']))
    if element.xpath('EXPRESSION').count == f.parameters.count

      element.xpath('EXPRESSION').each_with_index do |e, i|
        #puts f
        #puts i.to_s + f.parameters[i].type
        exp_check(e, f.parameters[i].type)
      end
      return f.type
    else
      puts 'function ' + element.at_xpath('IDENTIFIER')['src'].green + ' needs ' + f.parameters.count.to_s.green + ' arguments, got ' + element.xpath('EXPRESSION').count.to_s.red
      @error = true
      return f.type
    end
  else
    puts 'declaration of function ' + element.at_xpath('IDENTIFIER')['src'].red + ' not found'
    @error = true
    return ''
  end
end

def while_check(element)
  #deal with expresion
  #exp_check2(element.at_xpath('*[1]'))
  exp_check2(element.at_xpath('EXPRESSION/*'))
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
          puts @prev_var.green + ' and ' + e.green + ' types should be same(' + @same_type.red + ' ' + type.red + ' )'
          @error = true
        end
      end
    else
      puts e['src'].red + ' is not defined in for loop'
      @error = true
      break
    end
  end
  add_variable(element.at_xpath('ASSIG'), @same_type)
  check_block(element.at_xpath('BLOCK'))
  @variables.pop(@variables.size - element.ancestors.count).to_s
end

def if_check(element)
  exp_check2(element.at_xpath('EXPRESSION/*'))
  check_block(element.at_xpath('BLOCK'))
  if (el = element.at_xpath('ELSE'))
    check_block(el.at_xpath('BLOCK'))
  end
end

def sout_check(element)
  element.xpath('EXPRESSION').each { |e| exp_check(e, 'STRING')}
end

def sin_check(element)
  if (type = is_variable?(element.at_xpath('IDENTIFIER')['src']))
    unless type == 'STRING'
      puts "systemIn needs argument of type " + "string".green + " got" + type.red
      @error = true
    end
  else
    puts "systemIn got undefined argument " + element.at_xpath('IDENTIFIER')['src'].red
    @error = true
  end
end

def check_block (block)
  block.xpath('*').each do |element|
    case element.name
    when 'ASSIG'
      assig_check(element)
    when 'FUNCTIONCAL'
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
        if t = e.at_xpath('../../../TYPE')#["not(@class='VOID')"]
          #puts e
          #puts t
          #puts @functions
          exp_check(e, t['class'])
        elsif e.at_xpath('../../../../MAIN')
          puts 'Main has no return type'
          @error = true
        else
          puts "functions' #{e.at_xpath('../../../IDENTIFIER')['src']} return type is void"
          @error = true
        end
      end
    else
      puts 'unhandled statement: ' + element.name.yellow
      @error = true
    end
  end
  return if @variables.count < 2
  @variables.pop(@variables.size - block.ancestors.count).to_s
end

def check_function (fun_defs)
  fun_defs.each do |f|
    add_function(f)
    @variables.clear
  end
  fun_defs.each do |f|
    # puts f.name
    #puts f
    #p (is_function?(f.at_xpath('IDENTIFIER')['src']))
    @variables << (is_function?(f.at_xpath('IDENTIFIER')['src'])).parameters
    @all_variables << (is_function?(f.at_xpath('IDENTIFIER')['src'])).parameters
    check_return(f.at_xpath('BLOCK'))# if f.at_xpath('TYPE')#["not(@class='VOID')"]
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

def before_patch (doc)
  @types.each do |type|
    doc.xpath("//#{type}").each do |i| 
      i.name = 'TYPE'
      i['class'] = type
    end
  end
  doc.xpath('//RPAREN').each do |e|
    e.remove
  end
  doc.xpath('//EXPRESSION').each do |e|
    if e.children.count == 0
      e.remove
    else e.children.count == 1 && e.at_xpath('./EXPRESSION')
      #e.replace e.children #TODO pridek sita i after patch
    end
  end
  doc.xpath('//TERM|//SIMPLEEXPRESSION').each do |t|
    t.replace t.children
  end
  doc
end

def after_patch (doc)
  doc.xpath('//EXPRESSION').each do |t|
    t.replace t.children
  end
  doc.xpath('//FUNCTION').each do |f|
    f['class'] = (f.at_xpath('TYPE').remove)['class']
  end
  doc.xpath('//FUNCTION|//FUNCTIONCAL|//TYPE').each do |f|
    f['src'] = (f.at_xpath('IDENTIFIER').remove)['src']
  end
end
`java -jar ./out/artifacts/TransMet.jar test.felk`
doc = File.open('logfile.xml') { |f| Nokogiri::XML(f) }
root = before_patch(doc.root)

if (!(f = root.xpath('FUNCTION')).empty?)
  #p f
  check_function(f)
  #check_main(f)
else
  #puts 'No function definition'
end
check_main(root.at_xpath('MAIN'))
#Output
root = after_patch(doc.root)
doc = doc.to_xml( indent:2).gsub(/(\s*((<\/)|(<\?)).*)|[<>\/]/,'')
doc = doc.gsub(/(src|class)="(.+?)"/) {|s| s = $2}
puts 'Kintamieji:'.blue
puts @all_variables
puts "\nFunkcijos:".blue
puts @functions

if @error
  puts 'Rastos klaidos, nesaugoma tarpine forma'
  exit
end

doc2 = ''
@fun_line = Hash.new('Error not declared')
doc.each_line.with_index do |l,i|
  if (l.split)[0] == 'FUNCTION'
    @fun_line[(l.split)[2]] = i
  elsif (l.split)[0] == 'FUNCTIONCAL' 
    #puts (l.split)[1]
    #puts @fun_line[(l.split)[1]]
    doc2 << i.to_s + ' ' + l.sub((l.split)[1],
                                  @fun_line[(l.split)[1]].to_s)
    next
  end
   
  doc2 << i.to_s + ' ' + l
end
puts doc2
File.write('output.txt', doc)
