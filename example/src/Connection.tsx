import React, { useState } from 'react';
import {
  StyleSheet,
  TextInput,
  TouchableOpacity,
  Text,
  ScrollView,
  Dimensions,
} from 'react-native';
import QRCode from 'react-native-qrcode-svg';
import {
  startActivity,
  setupPushNotification,
  getTokenForQRCode,
} from 'react-native-rently-meari';

export default function Connection() {
  const [wifiName, setWifiName] = useState('rently');
  const [password, setPassword] = useState('$3cure6/1TR*nt1Y');
  const [fcmToken, setFcmToken] = useState('');
  const [token, setToken] = useState('');
  const [imageStr, setImageStr] = useState('');

  function createQRCodeContent(
    name: string,
    pwd: string,
    tok: string,
    isChimeSubDevice: boolean
  ): any {
    let format;
    let content;

    console.log('Harish', name, pwd, tok, isChimeSubDevice);

    if (!name || name.trim() === '') {
      format = `g:"G",t:"${tok}"`;
    } else {
      format = `s:"${name}",p:"${pwd}",t:"${tok}"`;
      if (isChimeSubDevice) {
        format += `,b:"1"`;
      }
    }

    content = format;

    console.log('Harish qr content', content);
    return content;
  }

  const getToken = async () => {
    const value = await getTokenForQRCode();

    console.log('Harish token', value);
    setToken(value);
  };

  const generateToken = async () => {
    const str = await createQRCodeContent(wifiName, password, token, false);

    console.log('Harish str', str);
    setImageStr(str);
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <TextInput
        placeholder="WiFi Name"
        onChangeText={(value) => setWifiName(value)}
        value={wifiName}
        style={styles.textInput}
      />
      <TextInput
        placeholder="Password"
        onChangeText={(value) => setPassword(value)}
        value={password}
        style={styles.textInput}
      />
      <TouchableOpacity style={styles.button} onPress={getToken}>
        <Text style={styles.buttonText}>Get Token</Text>
      </TouchableOpacity>
      {token ? <Text style={styles.tokenText}>{token}</Text> : null}
      <TouchableOpacity style={styles.button} onPress={generateToken}>
        <Text style={styles.buttonText}>Generate QR</Text>
      </TouchableOpacity>
      {imageStr ? (
        <QRCode
          value={imageStr}
          size={
            false
              ? Dimensions.get('window').height / 2.5
              : Dimensions.get('window').height / 4
          }
          bgColor="black"
          fgColor="white"
        />
      ) : null}

      <TextInput
        placeholder="FCM Token"
        onChangeText={(value) => setFcmToken(value)}
        value={fcmToken}
        style={styles.textInput}
      />
      <TouchableOpacity
        style={[styles.button, { marginTop: 10 }]}
        onPress={async () => {
          const tokenData = await setupPushNotification({ token: fcmToken });
          console.log('Harish tok', tokenData);
        }}
      >
        <Text style={styles.buttonText}>Setup FCM</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={[styles.button, { marginTop: 10 }]}
        onPress={() => {
          startActivity({ deviceId: '14439729' });
        }}
      >
        <Text style={styles.buttonText}>Start Meari</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
    backgroundColor: '#f2f2f2',
  },
  textInput: {
    height: 40,
    width: '100%',
    borderColor: '#cccccc',
    borderWidth: 1,
    borderRadius: 5,
    marginBottom: 20,
    paddingHorizontal: 10,
    backgroundColor: '#ffffff',
  },
  button: {
    height: 40,
    width: '100%',
    backgroundColor: '#007bff',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 5,
    marginBottom: 20,
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  tokenText: {
    color: '#333333',
    fontSize: 16,
    marginBottom: 20,
  },
});
