import * as React from 'react';

import {
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  Platform,
  Dimensions,
} from 'react-native';
import { startActivity, getTokenForQRCode } from 'react-native-rently-meari';
import QRCode from 'react-native-qrcode-svg';

export default function App() {
  const [generated, setGenerated] = React.useState(false);
  const [token, setToken] = React.useState('');
  const [imageStr, setImageStr] = React.useState('');

  function createQRCodeContent(
    wifiName: string,
    password: string,
    tok: string,
    isChimeSubDevice: boolean
  ): any {
    let format;
    let content;

    console.log('Harish', wifiName, password, tok, isChimeSubDevice);

    if (!wifiName || wifiName.trim() === '') {
      format = `g:"G",t:"${tok}"`;
    } else {
      format = `s:"${wifiName}",p:"${password}",t:"${tok}"`;
      if (isChimeSubDevice) {
        format += `,b:"1"`;
      }
    }

    content = format;
    setGenerated(true);
    return content;
  }

  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={styles.buttonStyles}
        onPress={() => {
          if (Platform.OS == 'ios') {
            console.log('Doorbell: Device is iOS');
          } else {
            startActivity({ deviceId: '14439729' });
          }
        }}
      >
        <Text>Start Meari Activity</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.buttonStyles}
        onPress={async () => {
          const value = await getTokenForQRCode();
          setToken(value);
        }}
      >
        <Text>Get Token</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.buttonStyles}
        onPress={() => {
          const str = createQRCodeContent(
            'rently',
            '$3cure6/1TR*nt1Y',
            token,
            false
          );
          console.log('Harish img Str', str);
          setImageStr(str);
        }}
      >
        <Text>Generate</Text>
      </TouchableOpacity>

      {generated && (
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
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonStyles: {
    height: 40,
    width: 120,
    backgroundColor: 'blue',
    borderRadius: 10,
    borderWidth: 1,
  },
});
