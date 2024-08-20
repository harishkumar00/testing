import * as React from 'react';
import {
  StyleSheet,
  View,
  TextInput,
  TouchableOpacity,
  Text,
} from 'react-native';
import { login, startActivity, openJSScreen } from 'react-native-rently-meari';
import Connection from './Connection';

export default function App() {
  const [email, setEmail] = React.useState('elango@mailinator.com');
  const [password, setPassword] = React.useState('12345678A');
  const [countryCode, setCountryCode] = React.useState('CN');
  const [phoneCode, setPhoneCode] = React.useState('86');
  const [isLoggedIn, setIsLoggedIn] = React.useState(false);

  const handleLogin = async () => {
    const result = await login({
      account: email,
      password: password,
      countryCode: countryCode,
      phoneCode: phoneCode,
    });

    console.log('Harish result', result);

    if (result) {
      setIsLoggedIn(true);
    } else {
      alert('Login failed, please try again');
    }
  };

  return (
    <>
      {isLoggedIn ? (
        <Connection />
      ) : (
        <View style={styles.container}>
          <Text style={styles.logo}>Meari Login</Text>
          <TextInput
            placeholder="Email"
            onChangeText={(value) => setEmail(value)}
            value={email}
            style={styles.textInput}
            keyboardType="email-address"
            autoCapitalize="none"
          />

          <TextInput
            placeholder="Password"
            onChangeText={(value) => setPassword(value)}
            value={password}
            style={styles.textInput}
            secureTextEntry
          />

          <TouchableOpacity
            style={styles.button}
            onPress={() => {
              console.log('Harish', email, password, countryCode, phoneCode);
              handleLogin();
            }}
          >
            <Text style={styles.buttonText}>Login</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.button, { marginTop: 10 }]}
            onPress={() => {
              startActivity({ deviceId: '14439729' });
            }}
          >
            <Text style={styles.buttonText}>Start Meari</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.button, { marginTop: 10 }]}
            onPress={() => {
              openJSScreen({ name: 'Harish' });
            }}
          >
            <Text style={styles.buttonText}>Open JS Screen</Text>
          </TouchableOpacity>
        </View>
      )}
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f2f2f2',
  },
  logo: {
    fontSize: 32,
    fontWeight: 'bold',
    marginBottom: 40,
    color: '#333333',
  },
  textInput: {
    height: 40,
    width: 300,
    borderColor: '#cccccc',
    borderWidth: 1,
    borderRadius: 5,
    marginBottom: 20,
    paddingHorizontal: 10,
    backgroundColor: '#ffffff',
  },
  button: {
    height: 40,
    width: 300,
    backgroundColor: '#007bff',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 5,
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
