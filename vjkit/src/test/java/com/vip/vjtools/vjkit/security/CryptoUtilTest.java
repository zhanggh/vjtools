package com.vip.vjtools.vjkit.security;

import static org.assertj.core.api.Assertions.*;

import com.vip.vjtools.vjkit.enums.CipherAlgorithms;
import com.vip.vjtools.vjkit.enums.KeyGeneratorType;
import com.vip.vjtools.vjkit.enums.KeyPairAlgorithms;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import com.vip.vjtools.vjkit.text.EncodeUtil;

import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Random;


public class CryptoUtilTest {
	@Test
	public void mac() {
		String input = "foo message";

		// key可为任意字符串
		// byte[] key = "a foo key".getBytes();
		byte[] key = CryptoUtil.generateHmacSha1Key();
		assertThat(key).hasSize(20);

		byte[] macResult = CryptoUtil.hmacSha1(input.getBytes(), key);
		System.out.println("hmac-sha1 key in hex      :" + EncodeUtil.encodeHex(key));
		System.out.println("hmac-sha1 in hex result   :" + EncodeUtil.encodeHex(macResult));

		assertThat(CryptoUtil.isMacValid(macResult, input.getBytes(), key)).isTrue();
	}

	@Test
	public void aes() {
		byte[] key = CryptoUtil.generateAesKey();
		assertThat(key).hasSize(16);
		String input = "foo message";

		byte[] encryptResult = CryptoUtil.aesEncrypt(input.getBytes(), key);
		String descryptResult = CryptoUtil.aesDecrypt(encryptResult, key);

		System.out.println("aes key in hex            :" + EncodeUtil.encodeHex(key));
		System.out.println("aes encrypt in hex result :" + EncodeUtil.encodeHex(encryptResult));
		assertThat(descryptResult).isEqualTo(input);
	}

	@Test
	public void aesWithIV() {
		byte[] key = CryptoUtil.generateAesKey();
		byte[] iv = CryptoUtil.generateIV();
		assertThat(key).hasSize(16);
		assertThat(iv).hasSize(16);
		String input = "foo message";

		byte[] encryptResult = CryptoUtil.aesEncrypt(input.getBytes(), key, iv);
		String descryptResult = CryptoUtil.aesDecrypt(encryptResult, key, iv);

		System.out.println("aes key in hex            :" + EncodeUtil.encodeHex(key));
		System.out.println("iv in hex                 :" + EncodeUtil.encodeHex(iv));
		System.out.println("aes encrypt in hex result :" + EncodeUtil.encodeHex(encryptResult));
		assertThat(descryptResult).isEqualTo(input);
	}

	@Test
	public void aesTest() throws UnsupportedEncodingException, NoSuchAlgorithmException {

		//安全提供者增加BC
		Security.addProvider(new BouncyCastleProvider());

		byte[] input = "Aes test".getBytes("UTF-8");
		byte[] key = KeyUtil.generateKey(128, KeyGeneratorType.AES);
		System.out.println(Arrays.toString(key));

		byte[] output = CryptoUtil.aesEncrypt(input, key, CipherAlgorithms.AES_ECB_PKCS5Padding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.aesDecrypt(output, key, CipherAlgorithms.AES_ECB_PKCS5Padding);
		System.out.println(new String(output));

		output = CryptoUtil.aesEncrypt(input, key, CipherAlgorithms.AES_ECB_ZeroBytePadding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.aesDecrypt(output, key, CipherAlgorithms.AES_ECB_ZeroBytePadding);
		System.out.println(new String(output));

		byte[] iv = CryptoUtil.generateIV();
		output = CryptoUtil.aesEncrypt(input, key, iv, CipherAlgorithms.AES_CBC_ZeroBytePadding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.aesDecrypt(output, key,iv, CipherAlgorithms.AES_CBC_ZeroBytePadding);
		System.out.println(new String(output));
	}

	@Test
	public void rcTest() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Security.addProvider(new BouncyCastleProvider());
		byte[] input = "rc test".getBytes("UTF-8");
		byte[] key = KeyUtil.generateKey(60, KeyGeneratorType.RC4);
		byte[] output = CryptoUtil.rc4Encrypt(input, key, CipherAlgorithms.RC4);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.rc4Decrypt(output, key, CipherAlgorithms.RC4);
		System.out.println(new String(output));

	}

	@Test
	public void desTest() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Security.addProvider(new BouncyCastleProvider());
		byte[] input = "des test".getBytes("UTF-8");
		byte[] key = KeyUtil.generateKey(56, KeyGeneratorType.DES);
		System.out.println(Arrays.toString(key));
		byte[] iv = CryptoUtil.generateDesIV();
		byte[] output = CryptoUtil.desEncrypt(input, key, iv, CipherAlgorithms.DES_CBC_ISO10126Padding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.desDecrypt(output,key,iv, CipherAlgorithms.DES_CBC_ISO10126Padding);
		assertThat(output).isEqualTo(input);

		output = CryptoUtil.desEncrypt(input, key,iv, CipherAlgorithms.DES_CFB_PKCS5Padding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.desDecrypt(output,key,iv, CipherAlgorithms.DES_CFB_PKCS5Padding);
		assertThat(output).isEqualTo(input);

		output = CryptoUtil.desEncrypt(input, key,iv, CipherAlgorithms.DES_OFB_PKCS5Padding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.desDecrypt(output,key,iv, CipherAlgorithms.DES_OFB_PKCS5Padding);
		assertThat(output).isEqualTo(input);

		output = CryptoUtil.desEncrypt(input, key,iv, CipherAlgorithms.DES_CTR_PKCS5Padding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.desDecrypt(output,key,iv, CipherAlgorithms.DES_CTR_PKCS5Padding);
		assertThat(output).isEqualTo(input);

		output = CryptoUtil.desEncrypt(input, key,iv, CipherAlgorithms.DES_PCBC_PKCS5Padding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.desDecrypt(output,key,iv, CipherAlgorithms.DES_PCBC_PKCS5Padding);
		assertThat(output).isEqualTo(input);
	}

	@Test
	public void des3Test() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		Security.addProvider(new BouncyCastleProvider());
		byte[] input = "des3 test".getBytes("UTF-8");
		byte[] key = KeyUtil.generateDes3Key();
		System.out.println(Arrays.toString(key));
		byte[] iv = CryptoUtil.generateDesIV();

		byte[] output = CryptoUtil.des3Encrypt(input, key,iv, CipherAlgorithms.Desede_CBC_ISO10126Padding);
		System.out.println(EncodeUtil.encodeHex(output));
		output = CryptoUtil.des3Decrypt(output, key,iv, CipherAlgorithms.Desede_CBC_ISO10126Padding);
		assertThat(output).isEqualTo(input);
	}

	@Test
	public void rsaTest() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		//非对称加密，使用公钥加密，私钥解密
		byte[] input = "rsa test".getBytes("UTF-8");
		KeyPair keyPair = KeyUtil.generateKeyPair(KeyPairAlgorithms.RSA, 512);
		//加密
		byte[] output = CryptoUtil.rsaEncrypt(input, keyPair.getPublic().getEncoded(), CipherAlgorithms.RSA_ECB_PKCS1Padding);
		System.out.println(EncodeUtil.encodeHex(output));
		//解密
		output = CryptoUtil.rsaDecrypt(output,keyPair.getPrivate().getEncoded(), CipherAlgorithms.RSA_ECB_PKCS1Padding);
		System.out.println(new String(output));
		assertThat(output).isEqualTo(input);


		//加密
		output = CryptoUtil.rsaEncrypt(input, keyPair.getPublic().getEncoded(), CipherAlgorithms.RSA_ECB_NoPadding);
		System.out.println(EncodeUtil.encodeHex(output));
		//解密
		output = CryptoUtil.rsaDecrypt(output, keyPair.getPrivate().getEncoded(), CipherAlgorithms.RSA_ECB_NoPadding);
		System.out.println(new String(output));
		assertThat(StringUtils.trimToEmpty(new String(output)).getBytes("UTF-8")).isEqualTo(input);

		byte[] iv = CryptoUtil.generateDesIV();
		//加密
		output = CryptoUtil.rsaEncrypt(input, keyPair.getPublic().getEncoded(), CipherAlgorithms.RSA_ECB_OAEPWithSHA1);
		System.out.println(EncodeUtil.encodeHex(output));
		//解密
		output = CryptoUtil.rsaDecrypt(output, keyPair.getPrivate().getEncoded(), CipherAlgorithms.RSA_ECB_OAEPWithSHA1);
		System.out.println(new String(output));
		assertThat(StringUtils.trimToEmpty(new String(output)).getBytes("UTF-8")).isEqualTo(input);

	}

	@Test
	public void pebTest() throws UnsupportedEncodingException {
		Security.addProvider(new BouncyCastleProvider());
		byte[] input = "PEB test".getBytes("UTF-8");
		String passwd = "1";
		byte[] salt = new byte[8];
		Random random = new Random();
		random.nextBytes(salt);

		//加密
		byte[] output = CryptoUtil.pbeEncrypt(input, passwd, salt, CipherAlgorithms.PBEWithSHAAndTwofish_CBC);
		System.out.println(EncodeUtil.encodeHex(output));

		//解密
		output = CryptoUtil.pbeDecrypt(output,passwd,salt,CipherAlgorithms.PBEWithSHAAndTwofish_CBC);
		System.out.println(new String(output));
		assertThat(output).isEqualTo(input);
	}



	@Test
	public void dhTest() throws Exception {
		//DiffieHellman算法加密
		Security.addProvider(new BouncyCastleProvider());
		byte[] input = "DiffieHellman test".getBytes("UTF-8");

		//生成甲方秘钥对
		KeyPair keyPair = KeyUtil.generateKeyPair(KeyPairAlgorithms.DiffieHellman, 512);
		//根据甲方公钥生成乙方秘钥对
		KeyPair keyPair2 = KeyUtil.generateKeyPair(KeyPairAlgorithms.DiffieHellman,
				((DHPublicKey) keyPair.getPublic()).getParams());
		//加密
		byte[] output = CryptoUtil.dhEncrypt(input, keyPair.getPublic(), keyPair2.getPrivate(),KeyGeneratorType.AES);
		System.out.println(EncodeUtil.encodeHex(output));

		//解密
		output = CryptoUtil.dhDencrypt(output, keyPair2.getPublic(), keyPair.getPrivate(), KeyGeneratorType.AES);
		System.out.println(new String(output));
		assertThat(output).isEqualTo(input);


		//----------------------DiffieHellman加密方式2

		//使用甲方公钥和乙方私钥生成的秘钥
		SecretKey secretKey = KeyUtil.generateKey( keyPair.getPublic(), keyPair2.getPrivate(), KeyGeneratorType.AES);
		//使用乙方公钥和甲方方私钥生成的秘钥
		SecretKey secretKey2 = KeyUtil.generateKey( keyPair2.getPublic(), keyPair.getPrivate(), KeyGeneratorType.AES);
		//双方生成的秘钥应该要相同
		assertThat(secretKey2.getEncoded()).isEqualTo(secretKey.getEncoded());

		System.out.println(EncodeUtil.encodeHex(secretKey.getEncoded()));
		System.out.println(EncodeUtil.encodeHex(secretKey2.getEncoded()));

		//加密
		output = CryptoUtil.aesEncrypt(input, secretKey.getEncoded(), CipherAlgorithms.AES_ECB_ISO10126Padding);
		System.out.println(EncodeUtil.encodeHex(output));

		//解密
		output = CryptoUtil.aesDecrypt(output, secretKey2.getEncoded(), CipherAlgorithms.AES_ECB_ISO10126Padding);
		System.out.println(new String(output));
		assertThat(output).isEqualTo(input);
	}


}
