task :default => :test

task :init do
  sh "mkdir -p classes"
  sh "mkdir -p test-classes"
end

task :clean do
  sh "rm -f *.jar"
  sh "rm -rf classes/*"
  sh "rm -rf test-classes/*"
  sh "rm -rf doc"
end

task :compile => :init do
  sh "javac -target 1.5 -d classes -cp #{lib_classpath} -Xlint:deprecation #{src_files}"
  cp_r "ssl", "classes"
end

desc "build a jar"
task :jar => :compile do
  sh "jar cvf #{jar_name} -C classes . > /dev/null"
end

task :compile_tests => [:init, :clean, :jar] do
  sh "javac -target 1.5 -Xlint:deprecation -d test-classes -cp #{jar_name}:#{lib_classpath} #{test_files}"
  cp_r "test/script", "test-classes", :preserve => true
  cp_r "test/ssl", "test-classes"
end

task :test => :compile_tests do
  sh "ant test"
end

desc "generate javadoc"
task :javadoc do
  excludes = [
    "com.braintreegateway.util",
    "com.braintreegateway.org"
  ]
  sh "javadoc -sourcepath src -subpackages com.braintreegateway -exclude #{excludes.join(":")} -d doc -overview overview.html"
end

def lib_classpath
  Dir.glob("lib/*.jar").join(":")
end

def src_files
  Dir.glob("src/**/*.java").join(" ")
end

def test_files
  Dir.glob("test/**/*.java").join(" ")
end

def jar_name
  "braintree-java-#{version}.jar"
end

def version
  contents = File.read('src/com/braintreegateway/BraintreeGateway.java')
  version = contents.slice(/VERSION = "(.*)"/, 1)
  raise "Cannot read version" if version.empty?
  version
end
