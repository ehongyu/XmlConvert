package com.casecentral.util;


/**
 * A partial translation from the MONO project C# code to Java by Hongyu Zhang.
 * Main credits are to the original C# code authors
 * 
 */
public class XmlConvert {

	public static String EncodeName(String name) {
		return EncodeName(name, false);
	}

	public static String DecodeName(String name) {
		if (name == null || name.length() == 0)
			return name;

		int pos = name.indexOf('_');
		if (pos == -1 || pos + 6 >= name.length())
			return name;

		if ((name.charAt(pos + 1) != 'X' && name.charAt(pos + 1) != 'x')
				|| name.charAt(pos + 6) != '_')
			return name.charAt(0) + DecodeName(name.substring(1));

		return name.substring(0, pos) + TryDecoding(name.substring(pos + 1));
	}

	private static boolean IsInvalid(char c, boolean firstOnlyLetter) {
		if (c == ':') // Special case. allowed in EncodeName, but encoded in
						// EncodeLocalName
			return false;

		if (firstOnlyLetter)
			return !XmlChar.IsFirstNameChar(c);
		else
			return !XmlChar.IsNameChar(c);
	}

	private static String EncodeName(String name, boolean nmtoken) {
		if (name == null || name.length() == 0)
			return name;

		StringBuilder sb = new StringBuilder();
		int length = name.length();
		for (int i = 0; i < length; i++) {
			char c = name.charAt(i);
			if (IsInvalid(c, i == 0 && !nmtoken))
				sb.append(String.format("_x%04X_", (int) c));
			else if (c == '_' && i + 6 < length && name.charAt(i + 1) == 'x'
					&& name.charAt(i + 6) == '_')
				sb.append("_x005F_");
			else
				sb.append(c);
		}
		return sb.toString();
	}

	private static String TryDecoding(String s) {
		if (s == null || s.length() < 6)
			return s;

		char c = '\uFFFF';
		try {
			// c = (char) Int32.Parse (s.substring (1, 4),
			// NumberStyles.HexNumber,
			// CultureInfo.InvariantCulture);
			
			// FIXME may have some localization issue but I don't think we need to deal with it right now
			
			c = (char) Integer.parseInt(s.substring(1, 5), 16);
		} catch (Exception e) {
			return s.charAt(0) + DecodeName(s.substring(1));
		}

		if (s.length() == 6)
			return Character.toString(c);
		return c + DecodeName(s.substring(6));
	}

}
