package net.seyarada.pandeloot.utils;

import java.util.Random;
import net.seyarada.pandeloot.Config;
import org.bukkit.util.Vector;

public class MathUtil {
  public static Vector calculateVelocity(Vector from, Vector to, double gravity, double heightGain) {
    int endGain = to.getBlockY() - from.getBlockY();
    double horizDist = Math.sqrt(distanceSquared(from, to));
    double maxGain = Math.max(heightGain, endGain + heightGain);
    double a = -horizDist * horizDist / 4.0D * maxGain;
    double b = horizDist;
    double c = -endGain;
    double slope = -b / 2.0D * a - Math.sqrt(b * b - 4.0D * a * c) / 2.0D * a;
    double vy = Math.sqrt(maxGain * (gravity + 0.0013675090252708D * heightGain));
    double vh = vy / slope;
    int dx = to.getBlockX() - from.getBlockX();
    int dz = to.getBlockZ() - from.getBlockZ();
    double mag = Math.sqrt((dx * dx + dz * dz));
    double dirx = dx / mag;
    double dirz = dz / mag;
    double vx = vh * dirx;
    double vz = vh * dirz;
    return new Vector(vx, vy, vz);
  }
  
  private static double distanceSquared(Vector from, Vector to) {
    double dx = (to.getBlockX() - from.getBlockX());
    double dz = (to.getBlockZ() - from.getBlockZ());
    return dx * dx + dz * dz;
  }
  
  public static double eval(final String str) {
    return (new Object() {
        int pos = -1;
        
        int ch;
        
        void nextChar() {
          this.ch = (++this.pos < str.length()) ? str.charAt(this.pos) : -1;
        }
        
        boolean eat(int charToEat) {
          for (; this.ch == 32; nextChar());
          if (this.ch == charToEat) {
            nextChar();
            return true;
          } 
          return false;
        }
        
        double parse() {
          nextChar();
          double x = parseExpression();
          if (this.pos < str.length())
            throw new RuntimeException("Unexpected: " + (char)this.ch); 
          return x;
        }
        
        double parseExpression() {
          double x = parseTerm();
          while (true) {
            for (; eat(43); x += parseTerm());
            if (eat(45)) {
              x -= parseTerm();
              continue;
            } 
            return x;
          } 
        }
        
        double parseTerm() {
          double x = parseFactor();
          while (true) {
            for (; eat(42); x *= parseFactor());
            if (eat(47)) {
              x /= parseFactor();
              continue;
            } 
            return x;
          } 
        }
        
        double parseFactor() {
          double x;
          if (eat(43))
            return parseFactor(); 
          if (eat(45))
            return -parseFactor(); 
          int startPos = this.pos;
          if (eat(40)) {
            x = parseExpression();
            eat(41);
          } else {
            if ((this.ch >= 48 && this.ch <= 57) || this.ch == 46)
              while (true) {
                if ((this.ch >= 48 && this.ch <= 57) || this.ch == 46) {
                  nextChar();
                  continue;
                } 
                x = Double.parseDouble(str.substring(startPos, this.pos));
                if (eat(94))
                  x = Math.pow(x, parseFactor()); 
                return x;
              }  
            if (this.ch >= 97 && this.ch <= 122) {
              for (; this.ch >= 97 && this.ch <= 122; nextChar());
              String func = str.substring(startPos, this.pos);
              x = parseFactor();
              if (func.equals("sqrt")) {
                x = Math.sqrt(x);
              } else if (func.equals("sin")) {
                x = Math.sin(Math.toRadians(x));
              } else if (func.equals("cos")) {
                x = Math.cos(Math.toRadians(x));
              } else if (func.equals("tan")) {
                x = Math.tan(Math.toRadians(x));
              } else {
                throw new RuntimeException("Unknown function: " + func);
              } 
            } else {
              throw new RuntimeException("Unexpected: " + (char)this.ch);
            } 
          } 
          if (eat(94))
            x = Math.pow(x, parseFactor()); 
          return x;
        }
      }).parse();
  }
  
  public static Vector getVelocity(double expoffset, double expheight) {
    Random random = new Random();
    return new Vector(
        Math.cos(random.nextDouble() * Math.PI * 2.0D) * expoffset, expheight, 
        Math.sin(random.nextDouble() * Math.PI * 2.0D) * expoffset);
  }
  
  public static String getDurationAsTime(long time) {
    int hours = (int)time / 3600;
    int temp = (int)time - hours * 3600;
    int mins = temp / 60;
    temp -= mins * 60;
    int secs = temp;
    if (hours > 0) {
      String str = Config.getHoursText();
      str = str.replace("%h%", String.valueOf(hours));
      str = str.replace("%m%", String.valueOf(mins));
      str = str.replace("%s%", String.valueOf(secs));
      return str;
    } 
    if (mins > 0) {
      String str = Config.getMinutesText();
      str = str.replace("%m%", String.valueOf(mins));
      str = str.replace("%s%", String.valueOf(secs));
      return str;
    } 
    String text = Config.getSecondsText();
    text = text.replace("%s%", String.valueOf(secs));
    return text;
  }
}
