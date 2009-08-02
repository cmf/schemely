/*
 * Copyright 2000-2009 Red Shark Technology
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.scheme.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.*;
import java.io.CharArrayReader;
import org.jetbrains.annotations.NotNull;

%%

%class _SchemeLexer
%implements SchemeTokenTypes, FlexLexer
%unicode
%public
%ignorecase

%function advance
%type IElementType

%eof{ return;
%eof}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// User code //////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

%{
  /*
  public final int getTokenStart(){
    return zzStartRead;
  }

  public final int getTokenEnd(){
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end,int initialState) {
    char [] buf = buffer.toString().substring(start,end).toCharArray();
    yyreset( new CharArrayReader( buf ) );
    yybegin(initialState);
  }
  
  public void reset(CharSequence buffer, int initialState){
    reset(buffer, 0, buffer.length(), initialState);
  }
  */
%}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// NewLines and spaces /////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Newline = \r | \n | \r\n                                    // NewLines
Whitespace = " " | \t | \f | {Newline}                       // Whitespaces

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////      integers and floats     /////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

HexDigit = [0-9A-Fa-f]
Digit = [0-9]
OctDigit = [0-9]
BinDigit = [0-1]

HexRadix = "#x"
DecRadix = ("#d")?
OctRadix = "#o"
BinRadix = "#b"

Exactness = ("#i" | "#e")?
Sign = ("+" | "-")?

ExponentMarker = e | s | f | d | l
Exponent = {ExponentMarker} {Sign} {Digit}+
Suffix = {Exponent}?

HexNum = {HexPrefix} {HexComplex}
HexComplex = {HexReal}
    | {HexReal} "@" {HexReal}
    | {HexReal} "+" {HexImag}
    | {HexReal} "-" {HexImag}
    | "+"{HexImag}
    | "-"{HexImag}
HexImag = {HexUReal} i
HexReal = {Sign} {HexUReal}
HexUReal = {HexUInteger} | {HexUInteger} "/" {HexUInteger}
HexUInteger = {HexDigit}+ "#"*
HexPrefix = ({HexRadix} {Exactness}) | ({Exactness} {HexRadix})

DecNum = {DecPrefix} {DecComplex}
DecComplex = {DecReal}
    | {DecReal} "@" {DecReal}
    | {DecReal} "+" {DecImag}
    | {DecReal} "-" {DecImag}
    | "+"{DecImag}
    | "-"{DecImag}
DecImag = {DecUReal} i
DecReal = {Sign} {DecUReal}
DecUReal = {DecUInteger} | {DecUInteger} "/" {DecUInteger} | {Decimal}
DecUInteger = {Digit}+ "#"*
DecPrefix = ({DecRadix} {Exactness}) | ({Exactness} {DecRadix})

OctNum = {OctPrefix} {OctComplex}
OctComplex = {OctReal}
    | {OctReal} "@" {OctReal}
    | {OctReal} "+" {OctImag}
    | {OctReal} "-" {OctImag}
    | "+"{OctImag}
    | "-"{OctImag}
OctImag = {OctUReal} i
OctReal = {Sign} {OctUReal}
OctUReal = {OctUInteger} | {OctUInteger} "/" {OctUInteger}
OctUInteger = {OctDigit}+ "#"*
OctPrefix = ({OctRadix} {Exactness}) | ({Exactness} {OctRadix})

BinNum = {BinPrefix} {BinComplex}
BinComplex = {BinReal}
    | {BinReal} "@" {BinReal}
    | {BinReal} "+" {BinImag}
    | {BinReal} "-" {BinImag}
    | "+"{BinImag}
    | "-"{BinImag}
BinImag = {BinUReal} i
BinReal = {Sign} {BinUReal}
BinUReal = {BinUInteger} | {BinUInteger} "/" {BinUInteger}
BinUInteger = {BinDigit}+ "#"*
BinPrefix = ({BinRadix} {Exactness}) | ({Exactness} {BinRadix})

Decimal = {DecUInteger} {Exponent}
    | "." {Digit}+ "#"* {Suffix}
    | {Digit}+ "." {Digit}* "#"* {Suffix}
    | {Digit}+ "#"+ "." "#"* {Suffix}

Number = {HexNum} | {DecNum} | {OctNum} | {BinNum}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Parens, Squares, Curleys, Quotes /////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

LParen = "("
RParen = ")"
LSquare = "["
RSquare = "]"
LCurly = "{"
RCurly = "}"
LVector = "#("

Quote = "'"
BackQuote = "`"
Comma = ","
CommaAt = ",@"

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Strings /////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

StringChar = "\\\"" | "\\\\" | [^\\\"]
String = \" {StringChar}* \"

Character = "#\\" . | "#\\newline" | "#\\space"

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// Comments ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Comment = ";" [^\r\n]*

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////      identifiers      ////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Letter = [A-Z] | [a-z]

Initial = {Letter} | "!" | "$" | "%"  | "&" | "*" | "/" | ":"  | "<" | "=" | ">" | "?"  | "~" | "_" | "^"
Subsequent = {Initial} | {Digit} | "."  | "+" | "-" | "@"
Identifier = {Initial} { Subsequent}* | "+" | "-" | "..."

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////      predefined      ////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

True = "#t"
False = "#f"

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////  states ///////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

%xstate YYINITIAL

%%

<YYINITIAL>{
  "quote"                                   {  return QUOTE; }
  "quasiquote"                              {  return QUASIQUOTE; }
  "unquote"                                 {  return UNQUOTE; }
  "unquote-splicing"                        {  return UNQUOTE_SPLICING; }
  "lambda"                                  {  return LAMBDA; }
  "define"                                  {  return DEFINE; }
  "define-syntax"                           {  return DEFINE_SYNTAX; }
  "if"                                      {  return IF; }
  "else"                                    {  return ELSE; }
  "let"                                     {  return LET; }
  "let*"                                    {  return LET_STAR; }
  "letrec"                                  {  return LETREC; }
  "set!"                                    {  return SET; }
  "begin"                                   {  return BEGIN; }
  "cond"                                    {  return COND; }
  "and"                                     {  return AND; }
  "or"                                      {  return OR; }
  "case"                                    {  return CASE; }
  "do"                                      {  return DO; }
  "delay"                                   {  return DELAY; }
  "let-syntax"                              {  return LET_SYNTAX; }
  "letrec-syntax"                           {  return LETREC_SYNTAX; }

  "=>"                                      {  return ARROW; }

  "..."                                     {  return DOTDOTDOT; }
  "."                                       {  return DOT; }
  
  {Comment}                                 {  return COMMENT; }
  
  {Whitespace}+                             {  return WHITESPACE; }
  {String}                                  {  return STRING_LITERAL; }

  {Character}                               {  return CHAR_LITERAL; }
  {True}                                    {  return TRUE; }
  {False}                                   {  return FALSE; }

  {Number}                                  {  return NUMBER_LITERAL; }

  {Identifier}                              {  return IDENTIFIER; }

  {Quote}                                   {  return QUOTE_MARK; }
  {BackQuote}                               {  return BACKQUOTE; }
  {Comma}                                   {  return COMMA; }
  {CommaAt}                                 {  return COMMA_AT; }

  {LVector}                                 {  return OPEN_VECTOR; }
  {LParen}                                  {  return LEFT_PAREN; }
  {RParen}                                  {  return RIGHT_PAREN; }
  {LSquare}                                 {  return LEFT_SQUARE; }
  {RSquare}                                 {  return RIGHT_SQUARE; }
  {LCurly}                                  {  return LEFT_CURLY; }
  {RCurly}                                  {  return RIGHT_CURLY; }

}

// Anything else should be marked as a bad char
.                                           {  return BAD_CHARACTER; }
